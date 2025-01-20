package com.example.daily_expense_tracker_app.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.daily_expense_tracker_app.R
import com.example.daily_expense_tracker_app.database.AppDatabase
import com.example.daily_expense_tracker_app.database.Expense
import com.example.daily_expense_tracker_app.databinding.ActivityMainBinding
import com.example.daily_expense_tracker_app.view.adapter.ExpenseAdapter
import com.example.daily_expense_tracker_app.viewmodel.ExpenseViewModel
import com.example.daily_expense_tracker_app.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var latestTmpUri: Uri? = null
    var expenseAmount_str: String = ""
    var expenseCategory_str: String = ""
    var expenseDate_str: String = ""
    var expenseDescription_str: String = ""
    private lateinit var expenseAdapter: ExpenseAdapter
    private var mainlist: ArrayList<Expense>? = null
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainlist = ArrayList()
      //  mainlist!!.add(1, Expense(1,"450","Rent","17/09/2023","Rent",""))
        RecyclerViewList()

        // initializing our view modal.
        expenseViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ExpenseViewModel::class.java)

        expenseViewModel.allExpenses?.observe(this, Observer { expenses ->
            expenseAdapter.setExpenses(expenses)
        })


        binding.profileImageView.setOnClickListener {
            openCamera()
        }

        binding.addExpenseButton.setOnClickListener {
            if (validateInputs()) {
                // Proceed with saving the expense
                addNewExpense()
                binding.expenseAmountEt.text?.clear()
                binding.expenseCategoryEt.text?.clear()
                binding.expenseDateEt.text = "Expense Date"
                binding.expenseDescriptionEt.text?.clear()
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()

            }
        }

        binding.expenseDateEt.setOnClickListener {
            showDatePickerDialog()
        }

        // Set up ActionBarDrawerToggle
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        // Set up click listener for Menu Button
        binding.MenuBTN.setOnClickListener {
            // Open the navigation drawer
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Handle navigation item clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)                }
                R.id.nav_profile -> {
                    // Launch ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
              /*  R.id.nav_settings -> {
                    Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
                }*/
            }
            // Close the drawer after selection
            drawerLayout.closeDrawers()
            true
        }
    }

    // Handle menu icon click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun addNewExpense() {
        // Example: Add a new expense
        val newExpense = Expense(
            id = 0, // Let Room handle the auto-generated ID
            amount = expenseAmount_str,
            category = expenseCategory_str,
           // date = System.currentTimeMillis().toString(),
            date = expenseDate_str,
            description = expenseDescription_str,
            imagePath = ""
        )
        expenseViewModel.insert(newExpense)
    }

    //For Recyclerview List
    private fun RecyclerViewList() {
        // Set LayoutManager
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter
        expenseAdapter = ExpenseAdapter{expense ->
            showExpenseDetails(expense)
        }
        // Set Adapter to RecyclerView
        binding.expenseRecyclerView.adapter = expenseAdapter
        expenseAdapter.setExpenses(mainlist!!)
    }

    // Function to handle item clicks
    private fun showExpenseDetails(expense: Expense) {
        //delete the expense
        expenseViewModel.delete(expense)
        Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show()
    }

    // Function to validate input fields
    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous error messages
        binding.expenseAmountEt.error = null
        binding.expenseCategoryEt.error = null
        binding.expenseDateEt.error = null
        binding.expenseDescriptionEt.error = null

        // Validate Expense Amount
        val expenseAmount = binding.expenseAmountEt.text.toString()
        expenseAmount_str=expenseAmount
        if (expenseAmount.isEmpty()) {
            binding.expenseAmountEt.error = "Expense Amount cannot be empty"
            isValid = false
        }

        // Validate Expense Category
        val expenseCategory = binding.expenseCategoryEt.text.toString()
        expenseCategory_str=expenseCategory
        if (expenseCategory.isEmpty()) {
            binding.expenseCategoryEt.error = "Expense Category cannot be empty"
            isValid = false
        }

        // Validate Expense Date
        val expenseDate = binding.expenseDateEt.text.toString()
        expenseDate_str=expenseDate
        if (expenseDate.isEmpty()) {
            binding.expenseDateEt.error = "Expense Date cannot be empty"
            isValid = false
        }

        // Validate Expense Description
        val expenseDescription = binding.expenseDescriptionEt.text.toString()
        expenseDescription_str=expenseDescription
        if (expenseDescription.isEmpty()) {
            binding.expenseDescriptionEt.error = "Expense Description cannot be empty"
            isValid = false
        }

        return isValid
    }

    private fun showDatePickerDialog() {
         val calendar: Calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Set the selected date in the calendar
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format the date
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                Log.d("MainActivity", "Selected Date: $formattedDate")
                binding.expenseDateEt.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    //for camera
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { binding.profileImageView.setImageBitmap(it) }
            } else {
                Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let { binding.profileImageView.setImageURI(it) }
            } else {
                Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show()
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


    //CustomDialog
/*
    fun showCustomDialog(context: Context) {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)

        // Create the dialog builder
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        // Create the dialog
        val dialog = dialogBuilder.create()

        // Initialize UI components
        val dialogTitle: TextView = dialogView.findViewById(R.id.dialogTitle)
        val dialogInput: EditText = dialogView.findViewById(R.id.dialogInput)

        // Set button click listener
        dialogButton.setOnClickListener {
            val inputText = dialogInput.text.toString()
            // Handle the input text as needed
            dialog.dismiss() // Close the dialog
        }

        // Show the dialog
        dialog.show()
    }
*/


}