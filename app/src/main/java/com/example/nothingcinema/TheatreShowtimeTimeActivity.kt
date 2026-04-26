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

class TheatreShowtimeTimeActivity : AppCompatActivity() {

    private lateinit var showtimeTimeRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var showtimeAdapter: ShowtimeTimeAdapterForTheatre
    private lateinit var movieNameTextView: TextView
    private lateinit var theatreNameTextView: TextView
    private lateinit var showtimeDateTextView: TextView
    private lateinit var moviePosterImageView: ImageView

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theatre_showtime_time)

        showtimeTimeRecyclerView = findViewById(R.id.showtimeTimeRecyclerView)
        movieNameTextView = findViewById(R.id.movieNameTextView)
        theatreNameTextView = findViewById(R.id.theatreNameTextView)
        showtimeDateTextView = findViewById(R.id.showtimeDateTextView)
        moviePosterImageView = findViewById(R.id.moviePosterImageView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        val movieId = intent.getLongExtra("movie_id", -1)
        val theatreId = intent.getLongExtra("theatre_id", -1)
        val showtimeDate = intent.getStringExtra("showtime_date")
        val reservationType = intent.getStringExtra("reservation_type") ?: "BOOK"

        if (movieId != -1L && theatreId != -1L && showtimeDate != null) {
            val movie = dbHelper.getMovieById(movieId)
            val theatre = dbHelper.getTheatreById(theatreId)

            movieNameTextView.text = movie?.name ?: "Unknown Movie"
            theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"
            showtimeDateTextView.text = showtimeDate

            movie?.posterImage?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                moviePosterImageView.setImageBitmap(bitmap)
            } ?: moviePosterImageView.setImageResource(R.drawable.ic_placeholder)

            val showtimes = dbHelper.getShowtimesByMovieIdAndDate(movieId, theatreId, showtimeDate)

            showtimeAdapter = ShowtimeTimeAdapterForTheatre(showtimes) { showtime ->
                val nextIntent = Intent(this, SeatSelectionActivity::class.java).apply {
                    putExtra("showtime_id", showtime.id)
                    putExtra("movie_id", showtime.movieId)
                    putExtra("theatre_id", showtime.theatreId)
                    putExtra("showtime_date", showtime.showtimeDate)
                    putExtra("showtime_time", showtime.showtimeTime)
                    putExtra("reservation_type", reservationType)
                }
                startActivity(nextIntent)
            }

            showtimeTimeRecyclerView.layoutManager = GridLayoutManager(this, 4)
            showtimeTimeRecyclerView.adapter = showtimeAdapter
        } else {
            Toast.makeText(this, "Invalid data received", Toast.LENGTH_SHORT).show()
        }
    }
}