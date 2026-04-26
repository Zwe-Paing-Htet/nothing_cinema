package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewShowtimeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var showtimeAdapter: ShowtimeAdapterForView
    private lateinit var backButton: ImageButton

    private lateinit var emptyShowtimesTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_showtime)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.showtimeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)
        emptyShowtimesTextView = findViewById(R.id.emptyShowtimesTextView)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

//        // Get all showtimes from the database
//        val showtimeList = dbHelper.getAllShowtimes()
//
//        // Initialize the adapter and pass the dbHelper instance and onShowtimeClick lambda
//        showtimeAdapter = ShowtimeAdapterForView(showtimeList, dbHelper) { showtime ->
//            // Handle showtime click (navigate to Showtime Details Activity)
//            val intent = Intent(this, ShowtimeDetailsActivity::class.java).apply {
//                putExtra("showtime_id", showtime.id) // Pass the showtime ID to the details page
//            }
//            startActivity(intent)
//        }
//
//        // Set the adapter to the RecyclerView
//        recyclerView.adapter = showtimeAdapter
//    }
        val showtimeList = dbHelper.getAllShowtimes()

        if (showtimeList.isEmpty()) {
            emptyShowtimesTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyShowtimesTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            showtimeAdapter = ShowtimeAdapterForView(showtimeList, dbHelper) { showtime ->
                val intent = Intent(this, ShowtimeDetailsActivity::class.java).apply {
                    putExtra("showtime_id", showtime.id)
                }
                startActivity(intent)
            }

            recyclerView.adapter = showtimeAdapter
        }
    }
}