package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TheatreShowtimeDateActivity : AppCompatActivity() {

    private lateinit var theatreNameTextView: TextView
    private lateinit var movieNameTextView: TextView
    private lateinit var moviePosterImageView: ImageView
    private lateinit var showtimesRecyclerView: RecyclerView

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var backButton: ImageButton

    private var theatreId: Long = -1
    private var movieId: Long = -1

    private lateinit var showtimeAdapter: ShowtimeDateAdapterForTheatre

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theatre_showtime_date)

        theatreNameTextView = findViewById(R.id.theatreNameTextView)
        movieNameTextView = findViewById(R.id.movieNameTextView)
        moviePosterImageView = findViewById(R.id.moviePosterImageView)
        showtimesRecyclerView = findViewById(R.id.showtimesRecyclerView)
        backButton = findViewById(R.id.backButton)

        dbHelper = DatabaseHelper(this)

        theatreId = intent.getLongExtra("theatre_id", -1)
        movieId = intent.getLongExtra("movie_id", -1)
        val reservationType = intent.getStringExtra("reservation_type") ?: "BOOK"

        if (theatreId != -1L && movieId != -1L) {
            val theatre = dbHelper.getTheatreById(theatreId)
            val movie = dbHelper.getMovieById(movieId)

            if (theatre != null && movie != null) {
                theatreNameTextView.text = theatre.name
                movieNameTextView.text = movie.name

                movie.posterImage?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    moviePosterImageView.setImageBitmap(bitmap)
                } ?: moviePosterImageView.setImageResource(R.drawable.ic_placeholder)

                val showtimes = dbHelper.getShowtimesByTheatreAndMovie(theatreId, movieId)

                showtimeAdapter = ShowtimeDateAdapterForTheatre(showtimes) { selectedDate ->
                    val nextIntent = Intent(this, TheatreShowtimeTimeActivity::class.java).apply {
                        putExtra("movie_id", movieId)
                        putExtra("theatre_id", theatreId)
                        putExtra("showtime_date", selectedDate)
                        putExtra("reservation_type", reservationType)
                    }
                    startActivity(nextIntent)
                }

                showtimesRecyclerView.layoutManager = GridLayoutManager(this, 4)
                showtimesRecyclerView.adapter = showtimeAdapter
            } else {
                Toast.makeText(this, "Theatre or movie not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid theatre or movie data", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}