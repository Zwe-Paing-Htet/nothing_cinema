package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewCinemaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var cinemaAdapter: CinemaAdapterForView

    private lateinit var backButton: ImageButton
    private lateinit var emptyCinemasTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_cinema)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.cinemaRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)
        emptyCinemasTextView = findViewById(R.id.emptyCinemasTextView)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get all cinemas from the database
//        val cinemaList = dbHelper.getAllCinemas()
//
//        // Initialize the adapter with the onCreateTheatreClick lambda
//        cinemaAdapter = CinemaAdapterForView(cinemaList) { cinema ->
//            // Handle Create Theatre button click
//            val intent = Intent(this, CreateTheatreActivity::class.java).apply {
//                putExtra("cinema_id", cinema.id)  // Pass the selected cinema's ID
//                putExtra("cinema_name", cinema.name)  // Pass the selected cinema's name
//            }
//            startActivity(intent)
//        }
//
//        // Set the adapter to the RecyclerView
//        recyclerView.adapter = cinemaAdapter
        val cinemaList = dbHelper.getAllCinemas()

        if (cinemaList.isEmpty()) {
            emptyCinemasTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyCinemasTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            cinemaAdapter = CinemaAdapterForView(cinemaList) { cinema ->
                val intent = Intent(this, CreateTheatreActivity::class.java).apply {
                    putExtra("cinema_id", cinema.id)
                    putExtra("cinema_name", cinema.name)
                }
                startActivity(intent)
            }

            recyclerView.adapter = cinemaAdapter
        }
    }
}