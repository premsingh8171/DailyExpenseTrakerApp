package com.example.daily_expense_tracker_app.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.daily_expense_tracker_app.R
import com.example.daily_expense_tracker_app.database.sharedpreference_cus.SharedPreferenceHelper
import com.example.daily_expense_tracker_app.databinding.ActivityMainBinding
import com.example.daily_expense_tracker_app.databinding.ActivityProfileBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var sharedPref : SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPreferenceHelper.getInstance(this)
        val savedImage = sharedPref.getImage("userImg")
        savedImage?.let {
            binding.circularImageView.setImageBitmap(it) // Display the saved image
        }

        // Get the BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        // Set initial state
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


        binding.backProfile.setOnClickListener {
            finish()
        }

        binding.circularImageView.setOnClickListener {
            toggleBottomSheet()
        }
    }

    private fun toggleBottomSheet() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.CameraBtn.setOnClickListener {
            openCamera()
            toggleBottomSheet()
        }
        binding.galleryBtn.setOnClickListener {
            openGallery()
            toggleBottomSheet()
        }
    }

    //for camera
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    binding.circularImageView.setImageBitmap(it)
                    sharedPref.saveImage("userImg", it) // Save image as Base64 in SharedPreferences
                }
            } else {
                Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    val bitmap = uriToBitmap(it) // Convert Uri to Bitmap
                    binding.circularImageView.setImageBitmap(bitmap)
                    bitmap?.let { img ->
                        sharedPref.saveImage("userImg", img) // Save image as Base64 in SharedPreferences
                    }
                }
            } else {
                Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    /**
     * Convert a Uri to Bitmap
     */
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }
}