package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BuyConfirmationActivity : AppCompatActivity() {

    private lateinit var successMessageTextView: TextView
    private lateinit var bookingDetailsTextView: TextView
    private lateinit var backToMainButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_confirmation)

        successMessageTextView = findViewById(R.id.successMessageTextView)
        bookingDetailsTextView = findViewById(R.id.bookingDetailsTextView)
        backToMainButton = findViewById(R.id.backToMainButton)

        val movieName = intent.getStringExtra("movie_name") ?: "Unknown Movie"
        val theatreName = intent.getStringExtra("theatre_name") ?: "Unknown Theatre"
        val cinemaName = intent.getStringExtra("cinema_name") ?: "Unknown Cinema"
        val selectedSeats = intent.getStringExtra("selected_seats") ?: "None"
        val totalPrice = intent.getDoubleExtra("total_price", 0.0)
        val customerName = intent.getStringExtra("customer_name") ?: "Unknown"
        val customerPhone = intent.getStringExtra("customer_phone") ?: "Unknown"
        val showtimeDate = intent.getStringExtra("showtime_date") ?: "Unknown Date"
        val showtimeTime = intent.getStringExtra("showtime_time") ?: "Unknown Time"
        val bookingTime = intent.getStringExtra("booking_time") ?: "Unknown Booking Time"
        val paymentMethod = intent.getStringExtra("payment_method") ?: "Unknown Payment"

//        successMessageTextView.text =
//            "Payment submitted successfully. Waiting for admin approval."
//
//        bookingDetailsTextView.text = """
//            Movie: $movieName
//
//            Theatre: $theatreName
//            Cinema: $cinemaName
//
//            Seats: $selectedSeats
//            Total Price: $totalPrice
//
//            Customer Name: $customerName
//            Phone Number: $customerPhone
//
//            Showtime Date: $showtimeDate
//            Showtime Time: $showtimeTime
//
//            Booking Time: $bookingTime
//            Payment Method: $paymentMethod
//            Payment Review Status: PENDING
//        """.trimIndent()

        successMessageTextView.text =
            "Payment submitted successfully. Your booking is waiting for admin approval."

        bookingDetailsTextView.text = """
Movie
$movieName

Theatre
$theatreName
$cinemaName

Seats
$selectedSeats

Total
%.2f MMK

Customer
$customerName
$customerPhone

Showtime
$showtimeDate • $showtimeTime

Submitted At
$bookingTime

Payment Method
$paymentMethod

Review Status
PENDING
""".trimIndent().format(totalPrice)

        backToMainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}