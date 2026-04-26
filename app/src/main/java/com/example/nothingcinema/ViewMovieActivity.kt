package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewMovieActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var movieAdapter: MovieAdapterForView
    private lateinit var backButton: ImageButton

    private lateinit var emptyMoviesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_movie)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.movieRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)
        emptyMoviesTextView = findViewById(R.id.emptyMoviesTextView)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get all movies from the database
//        val movieList = dbHelper.getAllMovies()
//
//        // Initialize the adapter with the onMovieClick lambda
//        movieAdapter = MovieAdapterForView(movieList) { movie ->
//            // Handle movie click (navigate to AddShowtimeActivity)
//            val intent = Intent(this, CreateShowtimeActivity::class.java).apply {
//                putExtra("movie_id", movie.id)  // Pass the movie ID
//                putExtra("movie_name", movie.name)  // If you want to pass movie name, do it here
//            }
//            startActivity(intent)
//        }
//
//        // Set the adapter to the RecyclerView
//        recyclerView.adapter = movieAdapter
//    }
        val movieList = dbHelper.getAllMovies()

        if (movieList.isEmpty()) {
            emptyMoviesTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyMoviesTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            movieAdapter = MovieAdapterForView(movieList) { movie ->
                val intent = Intent(this, CreateShowtimeActivity::class.java).apply {
                    putExtra("movie_id", movie.id)
                    putExtra("movie_name", movie.name)
                }
                startActivity(intent)
            }

            recyclerView.adapter = movieAdapter
        }
    }
}