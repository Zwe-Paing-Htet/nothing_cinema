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

class CinemaCollectionActivity : AppCompatActivity() {

    private lateinit var cinemaRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var cinemaAdapter: CinemaAdapterForCollection

    private lateinit var backButton: ImageButton
    private lateinit var emptyCinemasTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_collection)

        cinemaRecyclerView = findViewById(R.id.cinemaRecyclerView)
        backButton = findViewById(R.id.backButton)
        emptyCinemasTextView = findViewById(R.id.emptyCinemasTextView)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.nav_cinemas

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_movies -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_cinemas -> {
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchMovieActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val cinemaList = dbHelper.getAllCinemas()

        if (cinemaList.isEmpty()) {
            emptyCinemasTextView.visibility = View.VISIBLE
            cinemaRecyclerView.visibility = View.GONE
        } else {
            emptyCinemasTextView.visibility = View.GONE
            cinemaRecyclerView.visibility = View.VISIBLE

            cinemaAdapter = CinemaAdapterForCollection(cinemaList) { cinema ->
                val intent = Intent(this, CinemaDetailsActivity::class.java).apply {
                    putExtra("cinema_id", cinema.id)
                }
                startActivity(intent)
            }

            cinemaRecyclerView.layoutManager = LinearLayoutManager(this)
            cinemaRecyclerView.adapter = cinemaAdapter
        }
    }
}