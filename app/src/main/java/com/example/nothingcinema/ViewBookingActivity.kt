package com.example.nothingcinema

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewBookingActivity : AppCompatActivity() {

    private lateinit var stillBookingTitleTextView: TextView
    private lateinit var canceledBookingTitleTextView: TextView
    private lateinit var stillBookingsRecyclerView: RecyclerView
    private lateinit var canceledBookingsRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var stillBookingAdapter: BookingAdapter
    private lateinit var canceledBookingAdapter: BookingAdapter
    private lateinit var backButton: ImageButton

    private lateinit var emptyBookingsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_booking)

        stillBookingTitleTextView = findViewById(R.id.stillBookingTitleTextView)
        canceledBookingTitleTextView = findViewById(R.id.canceledBookingTitleTextView)
        stillBookingsRecyclerView = findViewById(R.id.stillBookingsRecyclerView)
        canceledBookingsRecyclerView = findViewById(R.id.canceledBookingsRecyclerView)
        backButton = findViewById(R.id.backButton)
        emptyBookingsTextView = findViewById(R.id.emptyBookingsTextView)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        loadBookings()
    }

    private fun loadBookings() {
        val bookingList = dbHelper.getAllBookings()

        if (bookingList.isEmpty()) {
            emptyBookingsTextView.visibility = View.VISIBLE
            stillBookingTitleTextView.visibility = View.GONE
            canceledBookingTitleTextView.visibility = View.GONE
            stillBookingsRecyclerView.visibility = View.GONE
            canceledBookingsRecyclerView.visibility = View.GONE
            return
        } else {
            emptyBookingsTextView.visibility = View.GONE
        }

        val activeBookings = bookingList.filter { it.bookingStatus == "ACTIVE" }
        val canceledBookings = bookingList.filter { it.bookingStatus == "CANCELED" }

        if (activeBookings.isNotEmpty()) {
            stillBookingAdapter = BookingAdapter(activeBookings, dbHelper, false)
            stillBookingsRecyclerView.layoutManager = LinearLayoutManager(this)
            stillBookingsRecyclerView.adapter = stillBookingAdapter
            stillBookingTitleTextView.visibility = View.VISIBLE
            stillBookingsRecyclerView.visibility = View.VISIBLE
        } else {
            stillBookingTitleTextView.visibility = View.GONE
            stillBookingsRecyclerView.visibility = View.GONE
        }

        if (canceledBookings.isNotEmpty()) {
            canceledBookingAdapter = BookingAdapter(canceledBookings, dbHelper, true)
            canceledBookingsRecyclerView.layoutManager = LinearLayoutManager(this)
            canceledBookingsRecyclerView.adapter = canceledBookingAdapter
            canceledBookingTitleTextView.visibility = View.VISIBLE
            canceledBookingsRecyclerView.visibility = View.VISIBLE
        } else {
            canceledBookingTitleTextView.visibility = View.GONE
            canceledBookingsRecyclerView.visibility = View.GONE
        }
    }
}