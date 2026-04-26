package com.example.nothingcinema

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class AdminTheatreShowtimesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var dbHelper: DatabaseHelper
    private var theatreId: Long = -1L
    private lateinit var emptyShowtimesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_theatre_showtimes)

        recyclerView = findViewById(R.id.showtimesRecyclerView)
        backButton = findViewById(R.id.backButton)
        emptyShowtimesTextView = findViewById(R.id.emptyShowtimesTextView)

        backButton.setOnClickListener {
            finish()
        }
        dbHelper = DatabaseHelper(this)

        theatreId = intent.getLongExtra("theatre_id", -1L)

        if (theatreId == -1L) {
            Toast.makeText(this, "Invalid theatre", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadShowtimes()
    }

//    private fun loadShowtimes() {
//        val showtimes = dbHelper.getShowtimesByTheatreId(theatreId)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = AdminShowtimeAdapter(showtimes, dbHelper)
//    }
    private fun loadShowtimes() {
        val showtimes = dbHelper.getShowtimesByTheatreId(theatreId)

        if (showtimes.isEmpty()) {
            emptyShowtimesTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyShowtimesTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = AdminShowtimeAdapter(showtimes, dbHelper)
        }
    }
}