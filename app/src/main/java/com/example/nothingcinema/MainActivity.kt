package com.example.nothingcinema

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var loggedInUserTextView: TextView
    private lateinit var btnProfile: Button
    private lateinit var logoutButton: Button
    private lateinit var backToWelcomeButton: Button
    private lateinit var notificationButton: Button
    private lateinit var homeTitleTextView: TextView
    private lateinit var themeSettingsButton: Button

    private lateinit var topWelcomeButton: ImageButton
    private lateinit var topProfileButton: ImageButton
    private lateinit var topNotificationButton: ImageButton

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nowShowingRecyclerView: RecyclerView
    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var nowShowingTitle: TextView
    private lateinit var upcomingTitle: TextView
    private lateinit var emptyMoviesTextView: TextView

    private lateinit var nowShowingAdapter: MovieAdapterForCollection
    private lateinit var upcomingAdapter: MovieAdapterForCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val moviePageButton: Button = findViewById(R.id.moviePageButton)
        val cinemaPageButton: Button = findViewById(R.id.cinemaPageButton)
        val searchButton: Button = findViewById(R.id.searchButton)

        logoutButton = findViewById(R.id.logoutButton)
        btnProfile = findViewById(R.id.btnProfile)
        backToWelcomeButton = findViewById(R.id.backToWelcomeButton)
        notificationButton = findViewById(R.id.notificationButton)

        topWelcomeButton = findViewById(R.id.topWelcomeButton)
        topProfileButton = findViewById(R.id.topProfileButton)
        topNotificationButton = findViewById(R.id.topNotificationButton)

        loggedInUserTextView = findViewById(R.id.loggedInUserTextView)
        homeTitleTextView = findViewById(R.id.homeTitleTextView)
        themeSettingsButton = findViewById(R.id.themeSettingsButton)

        nowShowingRecyclerView = findViewById(R.id.nowShowingRecyclerView)
        upcomingRecyclerView = findViewById(R.id.upcomingRecyclerView)
        nowShowingTitle = findViewById(R.id.nowShowingTitle)
        upcomingTitle = findViewById(R.id.upcomingTitle)
        emptyMoviesTextView = findViewById(R.id.emptyMoviesTextView)

        dbHelper = DatabaseHelper(this)

        moviePageButton.setOnClickListener {
            startActivity(Intent(this, MovieCollectionActivity::class.java))
        }

        cinemaPageButton.setOnClickListener {
            startActivity(Intent(this, CinemaCollectionActivity::class.java))
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchMovieActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, CustomerProfileActivity::class.java))
        }

        themeSettingsButton.setOnClickListener {
            startActivity(Intent(this, ThemeSettingsActivity::class.java))
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        backToWelcomeButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        topWelcomeButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        topProfileButton.setOnClickListener {
            startActivity(Intent(this, CustomerProfileActivity::class.java))
        }

        topNotificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        handleUserState()
        loadMoviesIntoHome()

        val bottomNavigationView =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_movies -> {
                    startActivity(Intent(this, MovieCollectionActivity::class.java))
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleUserState() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val username = sharedPreferences.getString("username", null)

        val isLoggedIn = userId != -1L && !username.isNullOrEmpty()

        if (isLoggedIn) {
            loggedInUserTextView.text = "Logged in as $username"
            homeTitleTextView.text = "Cinema Home"

            btnProfile.visibility = View.VISIBLE
            notificationButton.visibility = View.VISIBLE
            logoutButton.visibility = View.VISIBLE
            backToWelcomeButton.visibility = View.GONE

            topWelcomeButton.visibility = View.GONE
            topProfileButton.visibility = View.VISIBLE
            topNotificationButton.visibility = View.VISIBLE
        } else {
            loggedInUserTextView.text = "Logged in as Guest"
            homeTitleTextView.text = "Cinema Home"

            btnProfile.visibility = View.GONE
            notificationButton.visibility = View.GONE
            logoutButton.visibility = View.GONE
            backToWelcomeButton.visibility = View.VISIBLE

            topWelcomeButton.visibility = View.VISIBLE
            topProfileButton.visibility = View.GONE
            topNotificationButton.visibility = View.GONE
        }
    }

    private fun loadMoviesIntoHome() {
        val movieList = dbHelper.getAllMovies()

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

        nowShowingAdapter = MovieAdapterForCollection(nowShowingMovies) { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("movie_id", movie.id)
            }
            startActivity(intent)
        }

        upcomingAdapter = MovieAdapterForCollection(upcomingMovies) { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("movie_id", movie.id)
            }
            startActivity(intent)
        }

        nowShowingRecyclerView.layoutManager = GridLayoutManager(this, 2)
        nowShowingRecyclerView.adapter = nowShowingAdapter

        upcomingRecyclerView.layoutManager = GridLayoutManager(this, 2)
        upcomingRecyclerView.adapter = upcomingAdapter
    }

    private fun logoutUser() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}