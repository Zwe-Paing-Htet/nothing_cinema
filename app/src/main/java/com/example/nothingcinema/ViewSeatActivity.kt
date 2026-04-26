package com.example.nothingcinema

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewSeatActivity : AppCompatActivity() {

    private lateinit var seatedTheatresRecyclerView: RecyclerView
    private lateinit var seatRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var seatAdapter: SeatAdapter
    private lateinit var seatedTheatreAdapter: SeatedTheatreAdapter

    private lateinit var backButton: ImageButton
    private lateinit var emptySeatedTheatresTextView: TextView
    private lateinit var emptySeatsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_seat)

        seatedTheatresRecyclerView = findViewById(R.id.seatedTheatresRecyclerView)
        seatRecyclerView = findViewById(R.id.seatRecyclerView)
        backButton = findViewById(R.id.backButton)
        emptySeatedTheatresTextView = findViewById(R.id.emptySeatedTheatresTextView)
        emptySeatsTextView = findViewById(R.id.emptySeatsTextView)

        backButton.setOnClickListener {
            finish()
        }
        dbHelper = DatabaseHelper(this)

        loadSeatedTheatres()
        loadSeats()
    }

    override fun onResume() {
        super.onResume()
        loadSeatedTheatres()
        loadSeats()
    }

//    private fun loadSeatedTheatres() {
//        val theatreList = dbHelper.getTheatresWithSeats()
//
//        seatedTheatreAdapter = SeatedTheatreAdapter(theatreList)
//        seatedTheatresRecyclerView.layoutManager = LinearLayoutManager(this)
//        seatedTheatresRecyclerView.adapter = seatedTheatreAdapter
//    }

    private fun loadSeatedTheatres() {
        val theatreList = dbHelper.getTheatresWithSeats()

        if (theatreList.isEmpty()) {
            emptySeatedTheatresTextView.visibility = View.VISIBLE
            seatedTheatresRecyclerView.visibility = View.GONE
        } else {
            emptySeatedTheatresTextView.visibility = View.GONE
            seatedTheatresRecyclerView.visibility = View.VISIBLE
        }

        seatedTheatreAdapter = SeatedTheatreAdapter(theatreList)
        seatedTheatresRecyclerView.layoutManager = LinearLayoutManager(this)
        seatedTheatresRecyclerView.adapter = seatedTheatreAdapter
    }
//
//    private fun loadSeats() {
//        val seatList = dbHelper.getAllSeats()
//
//        if (seatList.isEmpty()) {
//            Toast.makeText(this, "No seats found", Toast.LENGTH_SHORT).show()
//        }
//
//        seatAdapter = SeatAdapter(seatList, dbHelper) {
//            loadSeatedTheatres()
//            loadSeats()
//        }
//
//        seatRecyclerView.layoutManager = LinearLayoutManager(this)
//        seatRecyclerView.adapter = seatAdapter
//    }
    private fun loadSeats() {
        val seatList = dbHelper.getAllSeats()

        if (seatList.isEmpty()) {
            emptySeatsTextView.visibility = View.VISIBLE
            seatRecyclerView.visibility = View.GONE
        } else {
            emptySeatsTextView.visibility = View.GONE
            seatRecyclerView.visibility = View.VISIBLE
        }

        seatAdapter = SeatAdapter(seatList, dbHelper) {
            loadSeatedTheatres()
            loadSeats()
        }

        seatRecyclerView.layoutManager = LinearLayoutManager(this)
        seatRecyclerView.adapter = seatAdapter
    }
}