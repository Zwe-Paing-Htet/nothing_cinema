package com.example.nothingcinema

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminShowtimeBookingsActivity : AppCompatActivity() {

    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var bookingAdapter: BookingAdapter
    private var showtimeId: Long = -1L
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_showtime_bookings)

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
        dbHelper = DatabaseHelper(this)

        showtimeId = intent.getLongExtra("showtime_id", -1L)

        if (showtimeId == -1L) {
            Toast.makeText(this, "Invalid showtime", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadBookings()
    }

    private fun loadBookings() {
        val bookingList = dbHelper.getBookingsByShowtimeId(showtimeId)
        val activeBookings = bookingList.filter { it.bookingStatus == "ACTIVE" }

        if (activeBookings.isNotEmpty()) {
            bookingAdapter = BookingAdapter(activeBookings, dbHelper, false)
            bookingsRecyclerView.layoutManager = LinearLayoutManager(this)
            bookingsRecyclerView.adapter = bookingAdapter

            bookingsRecyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        } else {
            bookingsRecyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
            emptyTextView.text = "No current bookings found for this showtime."
        }
    }
}