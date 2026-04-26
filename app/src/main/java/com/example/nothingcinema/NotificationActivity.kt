package com.example.nothingcinema

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.View

class NotificationActivity : AppCompatActivity() {

    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var bookingAdapter: NotificationBookingAdapter

    private lateinit var backButton: ImageButton

    private lateinit var emptyBookingsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        emptyBookingsTextView = findViewById(R.id.emptyBookingsTextView)

        dbHelper = DatabaseHelper(this)

        loadUserBookings()
    }

    private fun loadUserBookings() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)

        if (userId == -1L) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bookingList = dbHelper.getBookingsByUserId(userId)


        if (bookingList.isNotEmpty()) {
            emptyBookingsTextView.visibility = View.GONE
            bookingsRecyclerView.visibility = View.VISIBLE

            bookingAdapter = NotificationBookingAdapter(bookingList, dbHelper) {
                loadUserBookings()
            }
            bookingsRecyclerView.layoutManager = LinearLayoutManager(this)
            bookingsRecyclerView.adapter = bookingAdapter
        } else {
            emptyBookingsTextView.visibility = View.VISIBLE
            bookingsRecyclerView.visibility = View.GONE
            bookingsRecyclerView.adapter = null
        }
    }
}