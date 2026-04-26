package com.example.nothingcinema

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShowtimeDetailsActivity : AppCompatActivity() {

    private lateinit var showtimeDateTextView: TextView
    private lateinit var showtimeTimeTextView: TextView  // New TextView for showtime time
    private lateinit var movieNameTextView: TextView
    private lateinit var theatreNameTextView: TextView

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showtime_details)

        // Initialize views
        showtimeDateTextView = findViewById(R.id.showtimeDateTextView)
        showtimeTimeTextView = findViewById(R.id.showtimeTimeTextView)  // Initialize showtime time TextView
        movieNameTextView = findViewById(R.id.movieNameTextView)
        theatreNameTextView = findViewById(R.id.theatreNameTextView)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get the showtime_id passed from the previous activity
        val showtimeId = intent.getLongExtra("showtime_id", -1)

        if (showtimeId != -1L) {
            // Get showtime details from the database
            val showtime = dbHelper.getShowtimeById(showtimeId)

            if (showtime != null) {
                // Set the showtime details to the TextViews
                showtimeDateTextView.text = "Showtime: ${showtime.showtimeDate}"
                showtimeTimeTextView.text = "Time: ${showtime.showtimeTime}"  // Display the showtime time

                // Fetch the movie and theatre details
                val movie = dbHelper.getMovieById(showtime.movieId)
                val theatre = dbHelper.getTheatreById(showtime.theatreId)

                // Set movie and theatre details
                movieNameTextView.text = "Movie: ${movie?.name ?: "Unknown Movie"}"
                theatreNameTextView.text = "Theatre: ${theatre?.name ?: "Unknown Theatre"}"
            }
        }
    }
}