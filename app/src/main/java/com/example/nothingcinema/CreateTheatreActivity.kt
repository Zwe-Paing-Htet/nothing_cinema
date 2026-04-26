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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException

class CreateTheatreActivity : AppCompatActivity() {

    private lateinit var theatreNameEditText: EditText
    private lateinit var theatreTypeEditText: EditText
    private lateinit var theatreImageView: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var cinemaNameTextView: TextView  // TextView for Cinema Name

    private lateinit var backButton: ImageButton


    private lateinit var dbHelper: DatabaseHelper

    private var selectedImage: Bitmap? = null // Store selected image as Bitmap
    private var cinemaId: Long = -1 // To hold the cinema ID passed from the previous activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_theatre)

        // Initialize UI elements
        theatreNameEditText = findViewById(R.id.theatreNameEditText)
        theatreTypeEditText = findViewById(R.id.theatreTypeEditText)
        theatreImageView = findViewById(R.id.theatreImageView)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        saveButton = findViewById(R.id.saveButton)
        cinemaNameTextView = findViewById(R.id.cinemaNameTextView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get cinema ID and name passed from the previous activity
        cinemaId = intent.getLongExtra("cinema_id", -1)
        val cinemaName = intent.getStringExtra("cinema_name")

        // Set the Cinema Name in the TextView
//        cinemaNameTextView.text = "Add Theatre for: $cinemaName"
        cinemaNameTextView.text = cinemaName ?: "Unknown Cinema"

        // Set click listener for the "Choose Image" button
        chooseImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Set click listener for the Save button
        saveButton.setOnClickListener {
            // Get user input
            val theatreName = theatreNameEditText.text.toString()
            val theatreType = theatreTypeEditText.text.toString()

            // Validate inputs
            if (TextUtils.isEmpty(theatreName) || TextUtils.isEmpty(theatreType)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Resize the image before saving it
            val imageByteArray = selectedImage?.let {
                val resizedImage = resizeImage(it, 600, 400) // Resize the image (max width 800px, max height 600px)
                convertBitmapToByteArray(resizedImage)
            }

            // Insert the new theatre into the database
            val theatreId = dbHelper.insertTheatre(theatreName, theatreType, imageByteArray, cinemaId)

            if (theatreId != -1L) {
                Toast.makeText(this, "Theatre saved successfully!", Toast.LENGTH_SHORT).show()
                // Clear input fields after saving
                theatreNameEditText.text.clear()
                theatreTypeEditText.text.clear()
                theatreImageView.setImageResource(R.drawable.ic_placeholder)  // Reset image view
            } else {
                Toast.makeText(this, "Failed to save theatre", Toast.LENGTH_SHORT).show()
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
                theatreImageView.setImageBitmap(bitmap)
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

    // Resize the image to fit the screen (max width and height)
    private fun resizeImage(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
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

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}