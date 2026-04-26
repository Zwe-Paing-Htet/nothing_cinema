package com.example.nothingcinema

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReviewBuyPaymentsActivity : AppCompatActivity() {

    private lateinit var pendingPaymentsRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var paymentReviewAdapter: PaymentReviewAdapter

    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_buy_payments)

        pendingPaymentsRecyclerView = findViewById(R.id.pendingPaymentsRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        loadPendingPayments()
    }

    private fun loadPendingPayments() {
        val pendingBuyBookings = dbHelper.getPendingBuyBookings()

        if (pendingBuyBookings.isEmpty()) {
            pendingPaymentsRecyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
            emptyTextView.text = "No pending buy payments found."
            return
        }

        emptyTextView.visibility = View.GONE
        pendingPaymentsRecyclerView.visibility = View.VISIBLE

        paymentReviewAdapter = PaymentReviewAdapter(
            pendingBuyBookings,
            dbHelper
        ) {
            loadPendingPayments()
        }

        pendingPaymentsRecyclerView.layoutManager = LinearLayoutManager(this)
        pendingPaymentsRecyclerView.adapter = paymentReviewAdapter
    }
}