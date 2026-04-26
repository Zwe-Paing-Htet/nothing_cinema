package com.example.nothingcinema

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuyPaymentActivity : AppCompatActivity() {

    private lateinit var bookingDetailsTextView: TextView
    private lateinit var selectedPaymentMethodTextView: TextView
    private lateinit var receiptImageView: ImageView
    private lateinit var chooseReceiptButton: Button
    private lateinit var submitPaymentButton: Button

    private lateinit var kpayButton: Button
    private lateinit var wavePayButton: Button
    private lateinit var ayaPayButton: Button
    private lateinit var uabPayButton: Button
    private lateinit var backButton: ImageButton


    private lateinit var dbHelper: DatabaseHelper

    private var selectedPaymentMethod: String? = null
    private var receiptImageBytes: ByteArray? = null

    private var showtimeId: Long = -1L
    private var movieId: Long = -1L
    private var theatreId: Long = -1L
    private var userId: Long = -1L

    private lateinit var movieName: String
    private lateinit var theatreName: String
    private lateinit var cinemaName: String
    private lateinit var selectedSeats: String
    private var totalPrice: Double = 0.0
    private lateinit var customerName: String
    private lateinit var customerPhone: String
    private lateinit var showtimeDate: String
    private lateinit var showtimeTime: String
    private lateinit var selectedSeatIds: LongArray

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        receiptImageBytes = bytes
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        receiptImageView.setImageBitmap(bitmap)
                        Toast.makeText(this, "Payment receipt selected", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to read selected image", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_payment)

        bookingDetailsTextView = findViewById(R.id.bookingDetailsTextView)
        selectedPaymentMethodTextView = findViewById(R.id.selectedPaymentMethodTextView)
        receiptImageView = findViewById(R.id.receiptImageView)
        chooseReceiptButton = findViewById(R.id.chooseReceiptButton)
        submitPaymentButton = findViewById(R.id.submitPaymentButton)

        kpayButton = findViewById(R.id.kpayButton)
        wavePayButton = findViewById(R.id.wavePayButton)
        ayaPayButton = findViewById(R.id.ayaPayButton)
        uabPayButton = findViewById(R.id.uabPayButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        showtimeId = intent.getLongExtra("showtime_id", -1L)
        movieId = intent.getLongExtra("movie_id", -1L)
        theatreId = intent.getLongExtra("theatre_id", -1L)
        userId = intent.getLongExtra("user_id", -1L)

        movieName = intent.getStringExtra("movie_name") ?: "Unknown Movie"
        theatreName = intent.getStringExtra("theatre_name") ?: "Unknown Theatre"
        cinemaName = intent.getStringExtra("cinema_name") ?: "Unknown Cinema"
        selectedSeats = intent.getStringExtra("selected_seats") ?: "None"
        totalPrice = intent.getDoubleExtra("total_price", 0.0)
        customerName = intent.getStringExtra("customer_name") ?: "Unknown"
        customerPhone = intent.getStringExtra("customer_phone") ?: "Unknown"
        showtimeDate = intent.getStringExtra("showtime_date") ?: "Unknown Date"
        showtimeTime = intent.getStringExtra("showtime_time") ?: "Unknown Time"
        selectedSeatIds = intent.getLongArrayExtra("selected_seat_ids") ?: longArrayOf()


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
""".trimIndent().format(totalPrice)

        kpayButton.setOnClickListener {
            selectedPaymentMethod = "KPay"
            selectedPaymentMethodTextView.text = "Selected Payment • KPay"
            openPaymentApp("com.kbzbank.kpaycustomer")
        }

        wavePayButton.setOnClickListener {
            selectedPaymentMethod = "Wave Pay"
            selectedPaymentMethodTextView.text = "Selected Payment: Wave Pay"
            openPaymentApp("mm.com.wavemoney.wavepay")
        }

        ayaPayButton.setOnClickListener {
            selectedPaymentMethod = "AYA Pay"
            selectedPaymentMethodTextView.text = "Selected Payment: AYA Pay"
            openPaymentApp("com.ayaplus.subscriber")
        }

        uabPayButton.setOnClickListener {
            selectedPaymentMethod = "UAB Pay"
            selectedPaymentMethodTextView.text = "Selected Payment: UAB Pay"
            openPaymentApp("com.uab.uabbankpay")
        }

        chooseReceiptButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        submitPaymentButton.setOnClickListener {
            submitBuyPayment()
        }
    }

    private fun openPaymentApp(packageName: String) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(this, "Payment app is not installed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Payment app is not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitBuyPayment() {
        if (selectedPaymentMethod.isNullOrEmpty()) {
            Toast.makeText(this, "Please choose a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        if (receiptImageBytes == null) {
            Toast.makeText(this, "Please upload payment screenshot", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val bookingId = dbHelper.insertBooking(
            showtimeId = showtimeId,
            bookingTime = bookingTime,
            name = customerName,
            phoneNumber = customerPhone,
            userId = if (userId != -1L) userId else null,
            reservationType = "BUY",
            paymentMethod = selectedPaymentMethod,
            paymentScreenshot = receiptImageBytes,
            paymentReviewStatus = "PENDING"
        )

        if (bookingId == -1L) {
            Toast.makeText(this, "Failed to submit buy payment", Toast.LENGTH_SHORT).show()
            return
        }

        for (seatId in selectedSeatIds) {
            val result = dbHelper.insertBookingSeat(bookingId, seatId)
            if (result == -1L) {
                Toast.makeText(this, "Failed to save selected seats", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val intent = Intent(this, BuyConfirmationActivity::class.java).apply {
            putExtra("movie_name", movieName)
            putExtra("theatre_name", theatreName)
            putExtra("cinema_name", cinemaName)
            putExtra("selected_seats", selectedSeats)
            putExtra("total_price", totalPrice)
            putExtra("customer_name", customerName)
            putExtra("customer_phone", customerPhone)
            putExtra("showtime_date", showtimeDate)
            putExtra("showtime_time", showtimeTime)
            putExtra("booking_time", bookingTime)
            putExtra("payment_method", selectedPaymentMethod)
        }
        startActivity(intent)
        finish()
    }
}