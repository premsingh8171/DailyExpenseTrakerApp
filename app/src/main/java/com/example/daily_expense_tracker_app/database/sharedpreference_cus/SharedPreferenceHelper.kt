package com.example.daily_expense_tracker_app.database.sharedpreference_cus

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class SharedPreferenceHelper private constructor(context: Context) {

    companion object {
        private const val PREF_NAME = "MyAppPreferences" // Name of the SharedPreferences file
        @Volatile
        private var instance: SharedPreferenceHelper? = null

        // Singleton pattern to get the instance
        fun getInstance(context: Context): SharedPreferenceHelper {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferenceHelper(context).also { instance = it }
            }
        }
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Save a Bitmap value

    fun saveImage(key: String, bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        saveString(key, encodedImage)
    }

    fun getImage(key: String): Bitmap? {
        val encodedImage = getString(key, null.toString()) ?: return null
        val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


    // Save a string value
    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    // Retrieve a string value
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Save an integer value
    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    // Retrieve an integer value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Save a boolean value
    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    // Retrieve a boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Remove a specific key
    fun remove(key: String) {
        editor.remove(key).apply()
    }

    // Clear all stored data
    fun clear() {
        editor.clear().apply()
    }
}