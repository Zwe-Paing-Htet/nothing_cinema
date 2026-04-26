package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewTheatreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var theatreAdapter: TheatreAdapterForView
    private lateinit var backButton: ImageButton
    private lateinit var emptyTheatresTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_theatre)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.theatreRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)
        emptyTheatresTextView = findViewById(R.id.emptyTheatresTextView)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

//        // Get all theatres from the database
//        val theatreList = dbHelper.getAllTheatres()  // Get all theatres from the database
//
//        // Initialize the adapter and pass the onTheatreClick lambda function
//        theatreAdapter = TheatreAdapterForView(theatreList) { theatre ->
//            // Handle theatre click (navigate to Theatre Details Activity)
//            val intent = Intent(this, TheatreDetailsActivity::class.java).apply {
//                putExtra("theatre_id", theatre.id) // Pass the theatre ID to the details page
//            }
//            startActivity(intent)
//        }
//
//        // Set the adapter to the RecyclerView
//        recyclerView.adapter = theatreAdapter
//    }
        val theatreList = dbHelper.getAllTheatres()

        if (theatreList.isEmpty()) {
            emptyTheatresTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyTheatresTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            theatreAdapter = TheatreAdapterForView(theatreList) { theatre ->
                val intent = Intent(this, TheatreDetailsActivity::class.java).apply {
                    putExtra("theatre_id", theatre.id)
                }
                startActivity(intent)
            }

            recyclerView.adapter = theatreAdapter
        }
    }
}