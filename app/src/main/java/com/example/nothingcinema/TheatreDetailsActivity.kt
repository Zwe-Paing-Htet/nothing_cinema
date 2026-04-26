package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TheatreDetailsActivity : AppCompatActivity() {

    private lateinit var theatreNameTextView: TextView
    private lateinit var theatreTypeTextView: TextView
    private lateinit var theatreImageView: ImageView
    private lateinit var theatreCinemaNameTextView: TextView
    private lateinit var theatreCinemaAddressTextView: TextView
    private lateinit var movieContainer: LinearLayout
    private lateinit var backButton: ImageButton
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theatre_details)

        theatreNameTextView = findViewById(R.id.theatreNameTextView)
        theatreTypeTextView = findViewById(R.id.theatreTypeTextView)
        theatreImageView = findViewById(R.id.theatreImageView)
        theatreCinemaNameTextView = findViewById(R.id.theatreCinemaNameTextView)
        theatreCinemaAddressTextView = findViewById(R.id.theatreCinemaAddressTextView)
        movieContainer = findViewById(R.id.movieContainer)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        val theatreId = intent.getLongExtra("theatre_id", -1L)

        if (theatreId != -1L) {
            val theatre = dbHelper.getTheatreById(theatreId)

            if (theatre != null) {
                theatreNameTextView.text = theatre.name
                theatreTypeTextView.text = theatre.type ?: "Standard"

                theatre.image?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    theatreImageView.setImageBitmap(bitmap)
                } ?: theatreImageView.setImageResource(R.drawable.ic_placeholder)

                val cinema = dbHelper.getCinemaById(theatre.cinemaId)
                if (cinema != null) {
                    theatreCinemaNameTextView.text = cinema.name
                    theatreCinemaAddressTextView.text = cinema.fullAddress
                }

                showMoviesForTheatre(theatreId)
            }
        }
    }

    private fun showMoviesForTheatre(theatreId: Long) {
        val movieList = dbHelper.getMoviesForTheatre(theatreId)

        movieContainer.removeAllViews()

        for (movie in movieList) {
            val movieView = layoutInflater.inflate(
                R.layout.item_movie_theatre_collection,
                movieContainer,
                false
            )

            val moviePosterImageView = movieView.findViewById<ImageView>(R.id.moviePosterImageView)
            val movieNameTextView = movieView.findViewById<TextView>(R.id.movieNameTextView)
            val movieRuntimeTextView = movieView.findViewById<TextView>(R.id.movieRuntimeTextView)
            val movieGenreTextView = movieView.findViewById<TextView>(R.id.movieGenreTextView)
            val bookButton = movieView.findViewById<Button>(R.id.bookButton)
            val buyButton = movieView.findViewById<Button>(R.id.buyButton)

            movieNameTextView.text = movie.name
            movieRuntimeTextView.text = "${movie.runtime} min"
            movieGenreTextView.text = movie.genre ?: "Unknown genre"

            movie.posterImage?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                moviePosterImageView.setImageBitmap(bitmap)
            } ?: moviePosterImageView.setImageResource(R.drawable.ic_placeholder)

            bookButton.setOnClickListener {
                val intent = Intent(this, TheatreShowtimeDateActivity::class.java).apply {
                    putExtra("movie_id", movie.id)
                    putExtra("theatre_id", theatreId)
                    putExtra("reservation_type", "BOOK")
                }
                startActivity(intent)
            }

            buyButton.setOnClickListener {
                val intent = Intent(this, TheatreShowtimeDateActivity::class.java).apply {
                    putExtra("movie_id", movie.id)
                    putExtra("theatre_id", theatreId)
                    putExtra("reservation_type", "BUY")
                }
                startActivity(intent)
            }

            movieContainer.addView(movieView)
        }
    }
}