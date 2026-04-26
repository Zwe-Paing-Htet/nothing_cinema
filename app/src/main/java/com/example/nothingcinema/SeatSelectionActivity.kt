package com.example.nothingcinema

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var loggedInUserTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var movieNameTextView: TextView
    private lateinit var theatreNameTextView: TextView
    private lateinit var showtimeDateTextView: TextView
    private lateinit var showtimeTimeTextView: TextView
    private lateinit var moviePosterImageView: ImageView
    private lateinit var selectedSeatsTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var seatGridContainer: LinearLayout
    private lateinit var seatPriceContainer: LinearLayout
    private lateinit var customerNameEditText: EditText
    private lateinit var customerPhoneEditText: EditText
    private lateinit var bookShowtimeButton: Button
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var backButton: ImageButton
    private lateinit var topWelcomeButton: ImageButton
    private lateinit var topProfileButton: ImageButton
    private lateinit var topNotificationButton: ImageButton

    private var showtimeId: Long = -1L
    private var movieId: Long = -1L
    private var theatreId: Long = -1L
    private var showtimeDate: String? = null
    private var showtimeTime: String? = null
    private var reservationType: String = "BOOK"

    private lateinit var allSeats: List<DatabaseHelper.Seat>
    private var bookedSeatIds: Set<Long> = emptySet()
    private var boughtSeatIds: Set<Long> = emptySet()
    private val selectedSeatIds = mutableSetOf<Long>()
    private val selectedSeatLabels = mutableListOf<String>()
    private var totalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)

        loggedInUserTextView = findViewById(R.id.loggedInUserTextView)
        logoutButton = findViewById(R.id.logoutButton)
        movieNameTextView = findViewById(R.id.movieNameTextView)
        theatreNameTextView = findViewById(R.id.theatreNameTextView)
        showtimeDateTextView = findViewById(R.id.showtimeDateTextView)
        showtimeTimeTextView = findViewById(R.id.showtimeTimeTextView)
        moviePosterImageView = findViewById(R.id.moviePosterImageView)
        selectedSeatsTextView = findViewById(R.id.selectedSeatsTextView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        seatGridContainer = findViewById(R.id.seatGridContainer)
        seatPriceContainer = findViewById(R.id.seatPriceContainer)
        customerNameEditText = findViewById(R.id.customerNameEditText)
        customerPhoneEditText = findViewById(R.id.customerPhoneEditText)
        bookShowtimeButton = findViewById(R.id.bookShowtimeButton)
        backButton = findViewById(R.id.backButton)

        topWelcomeButton = findViewById(R.id.topWelcomeButton)
        topProfileButton = findViewById(R.id.topProfileButton)
        topNotificationButton = findViewById(R.id.topNotificationButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")
        loggedInUserTextView.text = "Logged in as $username"

        logoutButton.setOnClickListener {
            logoutUser()
        }

        topWelcomeButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        topProfileButton.setOnClickListener {
            startActivity(Intent(this, CustomerProfileActivity::class.java))
        }

        topNotificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        handleTopButtons()

        showtimeId = intent.getLongExtra("showtime_id", -1L)
        movieId = intent.getLongExtra("movie_id", -1L)
        theatreId = intent.getLongExtra("theatre_id", -1L)
        showtimeDate = intent.getStringExtra("showtime_date")
        showtimeTime = intent.getStringExtra("showtime_time")
        reservationType = intent.getStringExtra("reservation_type") ?: "BOOK"

        val movie = dbHelper.getMovieById(movieId)
        val theatre = dbHelper.getTheatreById(theatreId)

        movieNameTextView.text = movie?.name ?: "Unknown Movie"
        theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"
        showtimeDateTextView.text = showtimeDate ?: "Unknown Date"
        showtimeTimeTextView.text = showtimeTime ?: "Unknown Time"

        movie?.posterImage?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            moviePosterImageView.setImageBitmap(bitmap)
        } ?: moviePosterImageView.setImageResource(R.drawable.ic_placeholder)

        bookShowtimeButton.text =
            if (reservationType == "BUY") "Continue to Payment" else "Confirm Booking"

        loadSeatGrid()
        loadSeatPriceLegend()

        bookShowtimeButton.setOnClickListener {
            if (!isUserLoggedIn()) {
                showLoginRegisterDialog()
            } else {
                showBookingConfirmationDialog()
            }
        }
    }

    private fun handleTopButtons() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val username = sharedPreferences.getString("username", null)
        val isLoggedIn = userId != -1L && !username.isNullOrEmpty()

        if (isLoggedIn) {
            topWelcomeButton.visibility = View.GONE
            topProfileButton.visibility = View.VISIBLE
            topNotificationButton.visibility = View.VISIBLE
        } else {
            topWelcomeButton.visibility = View.VISIBLE
            topProfileButton.visibility = View.GONE
            topNotificationButton.visibility = View.GONE
        }
    }

    private fun seatColor(colorResId: Int): Int {
        return ContextCompat.getColor(this, colorResId)
    }

    private fun styleSeatButton(button: Button, colorResId: Int) {
        button.setBackgroundColor(seatColor(colorResId))
        button.setTextColor(seatColor(R.color.nc_text_primary))
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val username = sharedPreferences.getString("username", null)
        return userId != -1L && !username.isNullOrEmpty()
    }

    private fun showLoginRegisterDialog() {
        val actionText = if (reservationType == "BUY") "buy" else "book"

        AlertDialog.Builder(this)
            .setTitle("Login Required")
            .setMessage("To continue with $actionText, please login or register first.")
            .setPositiveButton("Login") { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .setNeutralButton("Register") { _, _ ->
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSeatLimitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Seat Limit Reached")
            .setMessage("You can select up to 4 seats only for one booking.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showBookingConfirmationDialog() {
        if (selectedSeatIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
            return
        }

        val title = if (reservationType == "BUY") "Confirm Payment" else "Confirm Booking"
        val message = if (reservationType == "BUY") {
            "Are you sure you want to continue to payment for the selected seats?"
        } else {
            "Are you sure you want to confirm this booking for the selected seats?"
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm") { _, _ ->
                bookSelectedSeats()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadSeatPriceLegend() {
        seatPriceContainer.removeAllViews()

        if (!::allSeats.isInitialized || allSeats.isEmpty()) {
            return
        }

        val seatsGroupedByType = allSeats.groupBy { it.seatTypeId }

        for ((seatTypeId, seatsOfType) in seatsGroupedByType) {
            val seatType = dbHelper.getSeatTypeById(seatTypeId) ?: continue

            val rowLetters = seatsOfType
                .mapNotNull { seat ->
                    seat.seatLabel.firstOrNull()?.uppercaseChar()?.toString()
                }
                .distinct()
                .sorted()

            val typeAndRowsText = if (rowLetters.isNotEmpty()) {
                "${seatType.name} (Rows ${rowLetters.joinToString(", ")})"
            } else {
                seatType.name
            }

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 10)
            }

            val typeText = TextView(this).apply {
                text = typeAndRowsText
                setTextColor(seatColor(R.color.nc_text_primary))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val priceText = TextView(this).apply {
                text = "%.0f MMK".format(seatType.price)
                setTextColor(seatColor(R.color.nc_text_secondary))
                textSize = 14f
            }

            row.addView(typeText)
            row.addView(priceText)
            seatPriceContainer.addView(row)
        }
    }

    private fun loadSeatGrid() {
        allSeats = dbHelper.getSeatsByTheatreId(theatreId)
        bookedSeatIds = dbHelper.getBookedSeatIdsByShowtimeId(showtimeId)
        boughtSeatIds = dbHelper.getBoughtSeatIdsByShowtimeId(showtimeId)

        seatGridContainer.removeAllViews()

        val groupedByRow = allSeats.groupBy { seat ->
            seat.seatLabel.firstOrNull()?.toString() ?: "?"
        }.toSortedMap()

        for ((rowName, seatList) in groupedByRow) {
            val rowTitle = TextView(this).apply {
                text = "Row $rowName"
                textSize = 16f
                setTextColor(seatColor(R.color.nc_text_secondary))
                setPadding(0, 12, 0, 8)
            }

            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.START
            }

            val sortedSeatList = seatList.sortedBy { extractSeatNumber(it.seatLabel) }

            for (seat in sortedSeatList) {
                val seatButton = Button(this).apply {
                    text = seat.seatLabel
                    textSize = 12f

                    val params = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    params.setMargins(4, 4, 4, 4)
                    layoutParams = params

                    when {
                        boughtSeatIds.contains(seat.id) -> {
                            styleSeatButton(this, R.color.nc_seat_bought)
                            isEnabled = false
                        }
                        bookedSeatIds.contains(seat.id) -> {
                            styleSeatButton(this, R.color.nc_seat_booked)
                            isEnabled = false
                        }
                        selectedSeatIds.contains(seat.id) -> {
                            styleSeatButton(this, R.color.nc_seat_selected)
                        }
                        else -> {
                            styleSeatButton(this, R.color.nc_seat_available)
                        }
                    }

                    setOnClickListener {
                        if (boughtSeatIds.contains(seat.id) || bookedSeatIds.contains(seat.id)) return@setOnClickListener

                        val seatType = dbHelper.getSeatTypeById(seat.seatTypeId)
                        val price = seatType?.price ?: 0.0

                        if (selectedSeatIds.contains(seat.id)) {
                            selectedSeatIds.remove(seat.id)
                            selectedSeatLabels.remove(seat.seatLabel)
                            totalPrice -= price
                            styleSeatButton(this, R.color.nc_seat_available)
                        } else {
                            if (selectedSeatIds.size >= 4) {
                                showSeatLimitDialog()
                                return@setOnClickListener
                            }

                            selectedSeatIds.add(seat.id)
                            selectedSeatLabels.add(seat.seatLabel)
                            totalPrice += price
                            styleSeatButton(this, R.color.nc_seat_selected)
                        }

                        updateSelectedSeatDisplay()
                    }
                }

                rowLayout.addView(seatButton)
            }

            seatGridContainer.addView(rowTitle)
            seatGridContainer.addView(rowLayout)
        }

        updateSelectedSeatDisplay()
    }

    private fun updateSelectedSeatDisplay() {
        selectedSeatsTextView.text = if (selectedSeatLabels.isEmpty()) {
            "Selected Seats: None"
        } else {
            "Selected Seats: ${selectedSeatLabels.sorted().joinToString(", ")}"
        }

        totalPriceTextView.text = "Total Price: %.2f MMK".format(totalPrice)
    }

    private fun bookSelectedSeats() {
        val customerName = customerNameEditText.text.toString().trim()
        val customerPhone = customerPhoneEditText.text.toString().trim()

        if (TextUtils.isEmpty(customerName) || TextUtils.isEmpty(customerPhone)) {
            Toast.makeText(this, "Please provide both name and phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSeatIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)

        val bookingTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val movie = dbHelper.getMovieById(movieId)
        val theatre = dbHelper.getTheatreById(theatreId)
        val cinema = theatre?.let { dbHelper.getCinemaById(it.cinemaId) }

        if (reservationType == "BOOK") {
            val bookingId = dbHelper.insertBooking(
                showtimeId = showtimeId,
                bookingTime = bookingTime,
                name = customerName,
                phoneNumber = customerPhone,
                userId = if (userId != -1L) userId else null,
                reservationType = reservationType
            )

            if (bookingId == -1L) {
                Toast.makeText(this, "Failed to create booking", Toast.LENGTH_SHORT).show()
                return
            }

            for (seatId in selectedSeatIds) {
                val result = dbHelper.insertBookingSeat(bookingId, seatId)
                if (result == -1L) {
                    Toast.makeText(this, "Failed to save selected seats", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            val intent = Intent(this, BookConfirmationActivity::class.java).apply {
                putExtra("movie_name", movie?.name ?: "Unknown Movie")
                putExtra("theatre_name", theatre?.name ?: "Unknown Theatre")
                putExtra("cinema_name", cinema?.name ?: "Unknown Cinema")
                putExtra("selected_seats", selectedSeatLabels.sorted().joinToString(", "))
                putExtra("total_price", totalPrice)
                putExtra("customer_name", customerName)
                putExtra("customer_phone", customerPhone)
                putExtra("showtime_date", showtimeDate ?: "Unknown Date")
                putExtra("showtime_time", showtimeTime ?: "Unknown Time")
                putExtra("booking_time", bookingTime)
            }
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, BuyPaymentActivity::class.java).apply {
                putExtra("showtime_id", showtimeId)
                putExtra("movie_id", movieId)
                putExtra("theatre_id", theatreId)
                putExtra("movie_name", movie?.name ?: "Unknown Movie")
                putExtra("theatre_name", theatre?.name ?: "Unknown Theatre")
                putExtra("cinema_name", cinema?.name ?: "Unknown Cinema")
                putExtra("selected_seat_ids", selectedSeatIds.toLongArray())
                putExtra("selected_seats", selectedSeatLabels.sorted().joinToString(", "))
                putExtra("total_price", totalPrice)
                putExtra("customer_name", customerName)
                putExtra("customer_phone", customerPhone)
                putExtra("showtime_date", showtimeDate ?: "Unknown Date")
                putExtra("showtime_time", showtimeTime ?: "Unknown Time")
                putExtra("user_id", if (userId != -1L) userId else -1L)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun extractSeatNumber(seatLabel: String): Int {
        return seatLabel.drop(1).toIntOrNull() ?: 0
    }

    private fun logoutUser() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}