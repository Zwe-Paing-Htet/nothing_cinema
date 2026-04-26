package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchMovieActivity : AppCompatActivity() {

    private lateinit var searchMovieEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var searchResultsCountTextView: TextView
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var movieList: List<DatabaseHelper.Movie>
    private lateinit var filteredMovieList: List<DatabaseHelper.Movie>

    private lateinit var backButton: ImageButton

    private lateinit var emptySearchTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        searchMovieEditText = findViewById(R.id.searchMovieEditText)
        searchButton = findViewById(R.id.searchButton)
        movieRecyclerView = findViewById(R.id.movieRecyclerView)
        searchResultsCountTextView = findViewById(R.id.searchResultsCountTextView)
        backButton = findViewById(R.id.backButton)
        emptySearchTextView = findViewById(R.id.emptySearchTextView)


        dbHelper = DatabaseHelper(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.nav_search

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_movies -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_cinemas -> {
                    startActivity(Intent(this, CinemaCollectionActivity::class.java))
                    true
                }
                R.id.nav_search -> {
                    true
                }
                else -> false
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        movieList = dbHelper.getAllMoviesSearch()
        filteredMovieList = emptyList()

        movieRecyclerView.layoutManager = LinearLayoutManager(this)

        searchResultsCountTextView.text = "Search Results: 0 movies"
        emptySearchTextView.visibility = View.GONE

        searchButton.setOnClickListener {
            val query = searchMovieEditText.text.toString().trim()

            if (query.isEmpty()) {
                filteredMovieList = emptyList()
                movieRecyclerView.adapter = null
                movieRecyclerView.visibility = View.GONE
                emptySearchTextView.visibility = View.GONE
                searchResultsCountTextView.text = "Search Results: 0 movies"
            } else {
                filterMovies(query)
            }
        }
    }

    private fun filterMovies(query: String) {
        filteredMovieList = movieList.filter {
            it.name.contains(query, ignoreCase = true)
        }

        val movieAdapter = MovieAdapterForTheatreCollection(
            filteredMovieList,
            onBookClick = { movie ->
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                startActivity(intent)
            },
            onBuyClick = { movie ->
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                startActivity(intent)
            }
        )

//        movieRecyclerView.adapter = movieAdapter
//        movieRecyclerView.visibility = View.VISIBLE

        movieRecyclerView.adapter = movieAdapter

        if (filteredMovieList.isEmpty()) {
            movieRecyclerView.visibility = View.GONE
            emptySearchTextView.visibility = View.VISIBLE
        } else {
            movieRecyclerView.visibility = View.VISIBLE
            emptySearchTextView.visibility = View.GONE
        }

        val resultCount = filteredMovieList.size
        searchResultsCountTextView.text =
            "Search Results: $resultCount movie${if (resultCount != 1) "s" else ""}"
    }
}