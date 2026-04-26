package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookConfirmationActivity : AppCompatActivity() {

    private lateinit var successMessageTextView: TextView
    private lateinit var infoMessageTextView: TextView
    private lateinit var movieValueTextView: TextView
    private lateinit var theatreValueTextView: TextView
    private lateinit var seatsValueTextView: TextView
    private lateinit var totalValueTextView: TextView
    private lateinit var customerValueTextView: TextView
    private lateinit var showtimeValueTextView: TextView
    private lateinit var bookedAtValueTextView: TextView
    private lateinit var backToMainButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_confirmation)

        successMessageTextView = findViewById(R.id.successMessageTextView)
        infoMessageTextView = findViewById(R.id.infoMessageTextView)
        movieValueTextView = findViewById(R.id.movieValueTextView)
        theatreValueTextView = findViewById(R.id.theatreValueTextView)
        seatsValueTextView = findViewById(R.id.seatsValueTextView)
        totalValueTextView = findViewById(R.id.totalValueTextView)
        customerValueTextView = findViewById(R.id.customerValueTextView)
        showtimeValueTextView = findViewById(R.id.showtimeValueTextView)
        bookedAtValueTextView = findViewById(R.id.bookedAtValueTextView)
        backToMainButton = findViewById(R.id.backToMainButton)
        backButton = findViewById(R.id.backButton)

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

        successMessageTextView.text = "Booking Confirmed"
        infoMessageTextView.text =
            "Please pay with cash at the cinema at least 2 hours before showtime or your booking may be canceled."

        movieValueTextView.text = movieName
        theatreValueTextView.text = "$theatreName\n$cinemaName"
        seatsValueTextView.text = selectedSeats
        totalValueTextView.text = "%.2f MMK".format(totalPrice)
        customerValueTextView.text = "$customerName\n$customerPhone"
        showtimeValueTextView.text = "$showtimeDate • $showtimeTime"
        bookedAtValueTextView.text = bookingTime

        backButton.setOnClickListener {
            finish()
        }

        backToMainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}