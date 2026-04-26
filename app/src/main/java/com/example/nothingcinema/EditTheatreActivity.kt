package com.example.nothingcinema

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

class EditTheatreActivity : AppCompatActivity() {

    private lateinit var theatreNameEditText: EditText
    private lateinit var theatreTypeEditText: EditText
    private lateinit var theatreImageView: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton


    private lateinit var dbHelper: DatabaseHelper

    private var theatreId: Long = -1
    private var selectedImage: Bitmap? = null // Store selected image as Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_theatre)

        // Initialize UI elements
        theatreNameEditText = findViewById(R.id.theatreNameEditText)
        theatreTypeEditText = findViewById(R.id.theatreTypeEditText)
        theatreImageView = findViewById(R.id.theatreImageView)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get theatre data from Intent
        theatreId = intent.getLongExtra("theatre_id", -1)
        val theatreName = intent.getStringExtra("theatre_name")
        val theatreType = intent.getStringExtra("theatre_type")
        val theatreImage = intent.getByteArrayExtra("theatre_image") // Get the theatre image (byte array)

        // Set the values to EditTexts
        theatreNameEditText.setText(theatreName)
        theatreTypeEditText.setText(theatreType)

        // Set the theatre image (if available)
        if (theatreImage != null) {
            val bitmap = BitmapFactory.decodeByteArray(theatreImage, 0, theatreImage.size)
            theatreImageView.setImageBitmap(bitmap)
            selectedImage = bitmap
        }

        // Set click listener for the "Choose Image" button
        chooseImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Save the updated theatre information
        saveButton.setOnClickListener {
            val newTheatreName = theatreNameEditText.text.toString()
            val newTheatreType = theatreTypeEditText.text.toString()

            // Validate inputs
            if (TextUtils.isEmpty(newTheatreName) || TextUtils.isEmpty(newTheatreType)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert selected image to byte array
            val imageByteArray = selectedImage?.let {
                val resizedImage = resizeImage(it, 600, 400) // Resize the image (max width 800px, max height 600px)
                convertBitmapToByteArray(resizedImage)
            }

            // Update the theatre in the database
            val updatedRows = dbHelper.updateTheatre(
                theatreId, newTheatreName, newTheatreType, imageByteArray
            )

            if (updatedRows > 0) {
                Toast.makeText(this, "Theatre updated successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity and go back to the previous screen
            } else {
                Toast.makeText(this, "Failed to update theatre", Toast.LENGTH_SHORT).show()
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

    // Resize the image
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