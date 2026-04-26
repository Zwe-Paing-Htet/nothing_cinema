package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var coverPosterImageView: ImageView
    private lateinit var moviePosterImageView: ImageView
    private lateinit var movieNameTextView: TextView
    private lateinit var movieRuntimeTextView: TextView
    private lateinit var movieGenreTextView: TextView
    private lateinit var movieDirectorTextView: TextView
    private lateinit var movieCastTextView: TextView
    private lateinit var movieDescriptionTextView: TextView
    private lateinit var openTrailerButton: Button
    private lateinit var theatresRecyclerView: RecyclerView

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var theatreAdapter: TheatreAdapterForMovieCollection

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        coverPosterImageView = findViewById(R.id.coverPosterImageView)
        moviePosterImageView = findViewById(R.id.moviePosterImageView)
        movieNameTextView = findViewById(R.id.movieNameTextView)
        movieRuntimeTextView = findViewById(R.id.movieRuntimeTextView)
        movieGenreTextView = findViewById(R.id.movieGenreTextView)
        movieDirectorTextView = findViewById(R.id.movieDirectorTextView)
        movieCastTextView = findViewById(R.id.movieCastTextView)
        movieDescriptionTextView = findViewById(R.id.movieDescriptionTextView)
        openTrailerButton = findViewById(R.id.openTrailerButton)
        theatresRecyclerView = findViewById(R.id.theatresRecyclerView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        val movieId = intent.getLongExtra("movie_id", -1L)

        if (movieId != -1L) {
            val movie = dbHelper.getMovieById(movieId)

            if (movie != null) {
                movieNameTextView.text = movie.name
                movieRuntimeTextView.text = "Runtime: ${movie.runtime} minutes"
                movieGenreTextView.text = "Genre: ${movie.genre}"
                movieDirectorTextView.text = "Director: ${movie.director ?: "Unknown"}"
                movieCastTextView.text = "Cast: ${movie.cast ?: "Unknown"}"
                movieDescriptionTextView.text =
                    "Description: ${movie.description ?: "No description available"}"

                movie.coverPosterImage?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    coverPosterImageView.setImageBitmap(bitmap)
                } ?: coverPosterImageView.setImageResource(R.drawable.ic_placeholder)

                movie.posterImage?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    moviePosterImageView.setImageBitmap(bitmap)
                } ?: moviePosterImageView.setImageResource(R.drawable.ic_placeholder)

                openTrailerButton.setOnClickListener {
                    val trailerUrl = movie.trailerUrl
                    if (!trailerUrl.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No trailer available", Toast.LENGTH_SHORT).show()
                    }
                }

                val theatreList = mutableListOf<DatabaseHelper.Theatre>()
                val uniqueTheatreIds = mutableSetOf<Long>()

                val showtimes = dbHelper.getShowtimesByMovieId(movieId)

                for (showtime in showtimes) {
                    val theatre = dbHelper.getTheatreById(showtime.theatreId)
                    theatre?.let {
                        if (!uniqueTheatreIds.contains(it.id)) {
                            theatreList.add(
                                DatabaseHelper.Theatre(
                                    it.id,
                                    it.name,
                                    it.type,
                                    it.image,
                                    it.cinemaId
                                )
                            )
                            uniqueTheatreIds.add(it.id)
                        }
                    }
                }

                theatreAdapter = TheatreAdapterForMovieCollection(
                    theatreList,
                    onBookClick = { theatre ->
                        val intent = Intent(this, TheatreShowtimeDateActivity::class.java).apply {
                            putExtra("theatre_id", theatre.id)
                            putExtra("movie_id", movieId)
                            putExtra("reservation_type", "BOOK")
                        }
                        startActivity(intent)
                    },
                    onBuyClick = { theatre ->
                        val intent = Intent(this, TheatreShowtimeDateActivity::class.java).apply {
                            putExtra("theatre_id", theatre.id)
                            putExtra("movie_id", movieId)
                            putExtra("reservation_type", "BUY")
                        }
                        startActivity(intent)
                    }
                )

                theatresRecyclerView.layoutManager = LinearLayoutManager(this)
                theatresRecyclerView.adapter = theatreAdapter
                theatresRecyclerView.isNestedScrollingEnabled = false
            }
        }
    }
}