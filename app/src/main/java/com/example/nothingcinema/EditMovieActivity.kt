package com.example.nothingcinema

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditMovieActivity : AppCompatActivity() {

    private lateinit var movieNameEditText: EditText
    private lateinit var runtimeEditText: EditText
    private lateinit var genreEditText: EditText
    private lateinit var directorEditText: EditText
    private lateinit var castEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var trailerUrlEditText: EditText
    private lateinit var statusRadioGroup: RadioGroup

    private lateinit var moviePosterImageView: ImageView
    private lateinit var coverPosterImageView: ImageView
    private lateinit var choosePosterButton: Button
    private lateinit var chooseCoverPosterButton: Button
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton

    private lateinit var dbHelper: DatabaseHelper

    private var movieId: Long = -1L
    private var selectedPosterImage: Bitmap? = null
    private var selectedCoverPosterImage: Bitmap? = null
    private var currentImagePickType: String = ""

    companion object {
        private const val IMAGE_PICK_CODE = 1001
        private const val PICK_POSTER = "POSTER"
        private const val PICK_COVER = "COVER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_movie)

        movieNameEditText = findViewById(R.id.movieNameEditText)
        runtimeEditText = findViewById(R.id.runtimeEditText)
        genreEditText = findViewById(R.id.genreEditText)
        directorEditText = findViewById(R.id.directorEditText)
        castEditText = findViewById(R.id.castEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        trailerUrlEditText = findViewById(R.id.trailerUrlEditText)
        statusRadioGroup = findViewById(R.id.statusRadioGroup)

        moviePosterImageView = findViewById(R.id.moviePosterImageView)
        coverPosterImageView = findViewById(R.id.coverPosterImageView)
        choosePosterButton = findViewById(R.id.choosePosterButton)
        chooseCoverPosterButton = findViewById(R.id.chooseCoverPosterButton)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        movieId = intent.getLongExtra("movie_id", -1L)

        if (movieId == -1L) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadMovieData()

        choosePosterButton.setOnClickListener {
            currentImagePickType = PICK_POSTER
            pickImageFromGallery()
        }

        chooseCoverPosterButton.setOnClickListener {
            currentImagePickType = PICK_COVER
            pickImageFromGallery()
        }

        saveButton.setOnClickListener {
            saveUpdatedMovie()
        }
    }

    private fun loadMovieData() {
        val movie = dbHelper.getMovieById(movieId)

        if (movie == null) {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        movieNameEditText.setText(movie.name)
        runtimeEditText.setText(movie.runtime.toString())
        genreEditText.setText(movie.genre)
        directorEditText.setText(movie.director ?: "")
        castEditText.setText(movie.cast ?: "")
        descriptionEditText.setText(movie.description ?: "")
        trailerUrlEditText.setText(movie.trailerUrl ?: "")

        if (movie.status == "Now Showing") {
            statusRadioGroup.check(R.id.nowShowingRadioButton)
        } else if (movie.status == "Upcoming") {
            statusRadioGroup.check(R.id.upcomingRadioButton)
        } else {
            statusRadioGroup.clearCheck()
        }

        if (movie.posterImage != null) {
            val bitmap = BitmapFactory.decodeByteArray(
                movie.posterImage,
                0,
                movie.posterImage.size
            )
            moviePosterImageView.setImageBitmap(bitmap)
            selectedPosterImage = bitmap
        } else {
            moviePosterImageView.setImageResource(R.drawable.ic_placeholder)
        }

        if (movie.coverPosterImage != null) {
            val bitmap = BitmapFactory.decodeByteArray(
                movie.coverPosterImage,
                0,
                movie.coverPosterImage.size
            )
            coverPosterImageView.setImageBitmap(bitmap)
            selectedCoverPosterImage = bitmap
        } else {
            coverPosterImageView.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun saveUpdatedMovie() {
        val movieName = movieNameEditText.text.toString().trim()
        val runtimeText = runtimeEditText.text.toString().trim()
        val genre = genreEditText.text.toString().trim()
        val director = directorEditText.text.toString().trim()
        val cast = castEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val trailerUrl = trailerUrlEditText.text.toString().trim()

        val selectedStatusId = statusRadioGroup.checkedRadioButtonId
        val movieStatus = when (selectedStatusId) {
            R.id.nowShowingRadioButton -> "Now Showing"
            R.id.upcomingRadioButton -> "Upcoming"
            else -> null
        }

        if (
            TextUtils.isEmpty(movieName) ||
            TextUtils.isEmpty(runtimeText) ||
            TextUtils.isEmpty(genre) ||
            TextUtils.isEmpty(director) ||
            TextUtils.isEmpty(cast) ||
            TextUtils.isEmpty(description) ||
            movieStatus == null
        ) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val runtime = try {
            runtimeText.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid runtime", Toast.LENGTH_SHORT).show()
            return
        }

        val posterByteArray = selectedPosterImage?.let {
            val resizedImage = resizeImage(it, 600, 400)
            convertBitmapToByteArray(resizedImage)
        }

        val coverPosterByteArray = selectedCoverPosterImage?.let {
            val resizedImage = resizeImage(it, 1000, 500)
            convertBitmapToByteArray(resizedImage)
        }

        val updatedRows = dbHelper.updateMovie(
            movieId = movieId,
            newMovieName = movieName,
            newRuntime = runtime,
            newGenre = genre,
            newDirector = director,
            newCast = cast,
            newDescription = description,
            trailerUrl = if (trailerUrl.isEmpty()) null else trailerUrl,
            status = movieStatus,
            posterImage = posterByteArray,
            coverPosterImage = coverPosterByteArray
        )

        if (updatedRows > 0) {
            Toast.makeText(this, "Movie updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to update movie", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resizeImage(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val aspectRatio: Float = image.width.toFloat() / image.height.toFloat()

        var newWidth = maxWidth
        var newHeight = maxHeight

        if (aspectRatio > 1) {
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, false)
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)

                    if (currentImagePickType == PICK_POSTER) {
                        selectedPosterImage = bitmap
                        moviePosterImageView.setImageBitmap(bitmap)
                    } else if (currentImagePickType == PICK_COVER) {
                        selectedCoverPosterImage = bitmap
                        coverPosterImageView.setImageBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}