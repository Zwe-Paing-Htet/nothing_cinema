package com.example.nothingcinema

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException

class CreateCinemaActivity : AppCompatActivity() {

    private lateinit var cinemaNameEditText: EditText
    private lateinit var townshipEditText: EditText
    private lateinit var fullAddressEditText: EditText
    private lateinit var googleMapUrlEditText: EditText
    private lateinit var cinemaPosterImageView: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton


    private lateinit var dbHelper: DatabaseHelper

    private var selectedImage: Bitmap? = null // Store selected image as Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cinema)

        // Initialize UI elements
        cinemaNameEditText = findViewById(R.id.cinemaNameEditText)
        townshipEditText = findViewById(R.id.townshipEditText)
        fullAddressEditText = findViewById(R.id.fullAddressEditText)
        googleMapUrlEditText = findViewById(R.id.googleMapUrlEditText)
        cinemaPosterImageView = findViewById(R.id.cinemaPosterImageView)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Set click listener for the "Choose Image" button
        chooseImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        fun resizeImage(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            // Calculate the aspect ratio
            val aspectRatio: Float = image.width.toFloat() / image.height.toFloat()

            var newWidth = maxWidth
            var newHeight = maxHeight

            // Adjust the width and height according to the aspect ratio
            if (aspectRatio > 1) { // Landscape
                newHeight = (maxWidth / aspectRatio).toInt()
            } else { // Portrait
                newWidth = (maxHeight * aspectRatio).toInt()
            }

            // Resize the bitmap
            return Bitmap.createScaledBitmap(image, newWidth, newHeight, false)
        }

        // Set click listener for Save button
        saveButton.setOnClickListener {
            // Get user input
            val cinemaName = cinemaNameEditText.text.toString()
            val township = townshipEditText.text.toString()
            val fullAddress = fullAddressEditText.text.toString()
            val googleMapUrl = googleMapUrlEditText.text.toString()  // Get the Google Map URL input

            // Validate inputs
            if (TextUtils.isEmpty(cinemaName) || TextUtils.isEmpty(township) || TextUtils.isEmpty(fullAddress)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Resize the image before saving it
            val imageByteArray = selectedImage?.let {
                val resizedImage = resizeImage(it, 600, 400) // Resize the image (max width 800px, max height 600px)
                convertBitmapToByteArray(resizedImage)
            }

            // Insert cinema into the database
            val cinemaId = dbHelper.insertCinema(cinemaName, township, fullAddress, googleMapUrl, imageByteArray)

            if (cinemaId != -1L) {
                Toast.makeText(this, "Cinema saved successfully!", Toast.LENGTH_SHORT).show()
                // Clear input fields after saving
                cinemaNameEditText.text.clear()
                townshipEditText.text.clear()
                fullAddressEditText.text.clear()
                googleMapUrlEditText.text.clear()  // Clear the Google Map URL field
                cinemaPosterImageView.setImageResource(R.drawable.ic_placeholder)  // Reset image view
            } else {
                Toast.makeText(this, "Failed to save cinema", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val selectedImageUri = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                cinemaPosterImageView.setImageBitmap(bitmap)
                selectedImage = bitmap  // Store the selected image
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Convert Bitmap to byte array
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}