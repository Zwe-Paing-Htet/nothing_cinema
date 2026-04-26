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

class EditCinemaActivity : AppCompatActivity() {

    private lateinit var cinemaNameEditText: EditText
    private lateinit var townshipEditText: EditText
    private lateinit var fullAddressEditText: EditText
    private lateinit var googleMapUrlEditText: EditText
    private lateinit var cinemaPosterImageView: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton


    private lateinit var dbHelper: DatabaseHelper

    private var cinemaId: Long = -1
    private var selectedImage: Bitmap? = null // Store selected image as Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cinema)

        // Initialize views
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

        // Get cinema data from Intent
        cinemaId = intent.getLongExtra("cinema_id", -1)
        val cinemaName = intent.getStringExtra("cinema_name")
        val township = intent.getStringExtra("cinema_township")
        val fullAddress = intent.getStringExtra("cinema_full_address")
        val googleMapUrl = intent.getStringExtra("cinema_google_map_url")
        val cinemaImage = intent.getByteArrayExtra("cinema_image") // Get the cinema poster (byte array)

        // Set the values to EditTexts
        cinemaNameEditText.setText(cinemaName)
        townshipEditText.setText(township)
        fullAddressEditText.setText(fullAddress)
        googleMapUrlEditText.setText(googleMapUrl)

        // Set the cinema poster (if available)
        if (cinemaImage != null) {
            val bitmap = BitmapFactory.decodeByteArray(cinemaImage, 0, cinemaImage.size)
            cinemaPosterImageView.setImageBitmap(bitmap)
            selectedImage = bitmap
        }

        // Set click listener for the "Choose Image" button
        chooseImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Save the updated cinema information
        saveButton.setOnClickListener {
            val newCinemaName = cinemaNameEditText.text.toString()
            val newTownship = townshipEditText.text.toString()
            val newFullAddress = fullAddressEditText.text.toString()
            val newGoogleMapUrl = googleMapUrlEditText.text.toString()

            // Validate inputs
            if (TextUtils.isEmpty(newCinemaName) || TextUtils.isEmpty(newTownship) || TextUtils.isEmpty(newFullAddress)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert selected image to byte array
            val imageByteArray = selectedImage?.let {
                val resizedImage = resizeImage(it, 600, 400) // Resize the image (max width 800px, max height 600px)
                convertBitmapToByteArray(resizedImage)
            }

            // Update the cinema in the database
            if (newCinemaName.isNotEmpty() && newTownship.isNotEmpty() && newFullAddress.isNotEmpty()) {
                val updatedRows = dbHelper.updateCinema(
                    cinemaId, newCinemaName, newTownship, newFullAddress, newGoogleMapUrl, imageByteArray
                )

                if (updatedRows > 0) {
                    Toast.makeText(this, "Cinema updated successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity and go back to the previous screen
                } else {
                    Toast.makeText(this, "Failed to update cinema", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
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