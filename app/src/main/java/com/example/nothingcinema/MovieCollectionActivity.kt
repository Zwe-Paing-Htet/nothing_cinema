package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MovieCollectionActivity : AppCompatActivity() {

    private lateinit var nowShowingRecyclerView: RecyclerView
    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nowShowingAdapter: MovieAdapterForCollection
    private lateinit var upcomingAdapter: MovieAdapterForCollection
    private lateinit var nowShowingTitle: TextView  // Reference to "Now Showing" title
    private lateinit var upcomingTitle: TextView  // Reference to "Upcoming" title
    private lateinit var backButton: ImageButton

    private lateinit var emptyMoviesTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_collection)

        // Initialize RecyclerViews
        nowShowingRecyclerView = findViewById(R.id.nowShowingRecyclerView)
        upcomingRecyclerView = findViewById(R.id.upcomingRecyclerView)

        // Initialize title TextViews
        nowShowingTitle = findViewById(R.id.nowShowingTitle)
        upcomingTitle = findViewById(R.id.upcomingTitle)
        backButton = findViewById(R.id.backButton)
        emptyMoviesTextView = findViewById(R.id.emptyMoviesTextView)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get all movies from the database
        val movieList = dbHelper.getAllMovies()

        // Filter movies by status
        val nowShowingMovies = movieList.filter { it.status == "Now Showing" }
        val upcomingMovies = movieList.filter { it.status == "Upcoming" }

        if (nowShowingMovies.isEmpty()) {
            nowShowingTitle.visibility = View.GONE
            nowShowingRecyclerView.visibility = View.GONE
        } else {
            nowShowingTitle.visibility = View.VISIBLE
            nowShowingRecyclerView.visibility = View.VISIBLE
        }

        if (upcomingMovies.isEmpty()) {
            upcomingTitle.visibility = View.GONE
            upcomingRecyclerView.visibility = View.GONE
        } else {
            upcomingTitle.visibility = View.VISIBLE
            upcomingRecyclerView.visibility = View.VISIBLE
        }

        if (nowShowingMovies.isEmpty() && upcomingMovies.isEmpty()) {
            emptyMoviesTextView.visibility = View.VISIBLE
        } else {
            emptyMoviesTextView.visibility = View.GONE
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.nav_movies

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_movies -> {
                    true
                }
                R.id.nav_cinemas -> {
                    startActivity(Intent(this, CinemaCollectionActivity::class.java))
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchMovieActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Initialize the adapter for Now Showing and Upcoming movies
        nowShowingAdapter = MovieAdapterForCollection(nowShowingMovies) { movie ->
            // Handle movie click (open Movie Details Activity)
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("movie_id", movie.id)
            }
            startActivity(intent)
        }
        upcomingAdapter = MovieAdapterForCollection(upcomingMovies) { movie ->
            // Handle movie click (open Movie Details Activity)
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("movie_id", movie.id)
            }
            startActivity(intent)

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

            bottomNavigationView.selectedItemId = R.id.nav_movies

            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_movies -> {
                        true
                    }
                    R.id.nav_cinemas -> {
                        startActivity(Intent(this, CinemaCollectionActivity::class.java))
                        true
                    }
                    R.id.nav_search -> {
                        startActivity(Intent(this, SearchMovieActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }

        // Set the adapters to the RecyclerViews
        nowShowingRecyclerView.layoutManager = GridLayoutManager(this, 2)
        nowShowingRecyclerView.adapter = nowShowingAdapter

        upcomingRecyclerView.layoutManager = GridLayoutManager(this, 2)
        upcomingRecyclerView.adapter = upcomingAdapter
    }
}