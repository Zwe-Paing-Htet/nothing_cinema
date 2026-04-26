package com.example.nothingcinema

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AdminSeatStatusActivity : AppCompatActivity() {

    private lateinit var showtimeTextView: TextView
    private lateinit var movieNameTextView: TextView
    private lateinit var theatreNameTextView: TextView
    private lateinit var totalSeatsTextView: TextView
    private lateinit var availableSeatsTextView: TextView
    private lateinit var soldSeatsTextView: TextView
    private lateinit var seatGridContainer: LinearLayout
    private lateinit var viewShowtimeBookingsButton: Button
    private lateinit var backButton: ImageButton

    private lateinit var dbHelper: DatabaseHelper

    private var showtimeId: Long = -1L
    private var movieId: Long = -1L
    private var theatreId: Long = -1L
    private var showtimeDate: String? = null
    private var showtimeTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_seat_status)

        showtimeTextView = findViewById(R.id.showtimeTextView)
        movieNameTextView = findViewById(R.id.movieNameTextView)
        theatreNameTextView = findViewById(R.id.theatreNameTextView)
        totalSeatsTextView = findViewById(R.id.totalSeatsTextView)
        availableSeatsTextView = findViewById(R.id.availableSeatsTextView)
        soldSeatsTextView = findViewById(R.id.soldSeatsTextView)
        seatGridContainer = findViewById(R.id.seatGridContainer)
        viewShowtimeBookingsButton = findViewById(R.id.viewShowtimeBookingsButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        showtimeId = intent.getLongExtra("showtime_id", -1L)
        movieId = intent.getLongExtra("movie_id", -1L)
        theatreId = intent.getLongExtra("theatre_id", -1L)
        showtimeDate = intent.getStringExtra("showtime_date")
        showtimeTime = intent.getStringExtra("showtime_time")

        loadSeatStatus()

        viewShowtimeBookingsButton.setOnClickListener {
            val intent = Intent(this, AdminShowtimeBookingsActivity::class.java).apply {
                putExtra("showtime_id", showtimeId)
            }
            startActivity(intent)
        }
    }

    private fun loadSeatStatus() {
        val movie = dbHelper.getMovieById(movieId)
        val theatre = dbHelper.getTheatreById(theatreId)
        val allSeats = dbHelper.getSeatsByTheatreId(theatreId)
        val bookedSeatIds = dbHelper.getBookedSeatIdsByShowtimeId(showtimeId)
        val boughtSeatIds = dbHelper.getBoughtSeatIdsByShowtimeId(showtimeId)


        showtimeTextView.text = "${showtimeDate ?: "Unknown Date"} • ${showtimeTime ?: "Unknown Time"}"
        movieNameTextView.text = movie?.name ?: "Unknown Movie"
        theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"

        val totalSeats = allSeats.size
        val soldSeats = allSeats.count { bookedSeatIds.contains(it.id) || boughtSeatIds.contains(it.id) }
        val availableSeats = totalSeats - soldSeats

        totalSeatsTextView.text = "Total Seats • $totalSeats"
        availableSeatsTextView.text = "Available Seats • $availableSeats"
        soldSeatsTextView.text = "Sold Out Seats • $soldSeats"

        seatGridContainer.removeAllViews()

        val groupedByRow = allSeats.groupBy {
            it.seatLabel.firstOrNull()?.toString() ?: "?"
        }.toSortedMap()

        for ((rowName, seatList) in groupedByRow) {

            val rowTitle = TextView(this).apply {
                text = "Row $rowName"
                textSize = 18f
                setTextColor(seatColor(R.color.nc_text_secondary))
                setPadding(0, 16, 0, 8)
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
                    isEnabled = false

                    val params = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    params.setMargins(4, 4, 4, 4)
                    layoutParams = params

                    when {
                        boughtSeatIds.contains(seat.id) -> {
                            setBackgroundColor(seatColor(R.color.nc_seat_bought))
                            setTextColor(seatColor(R.color.nc_text_primary))
                        }
                        bookedSeatIds.contains(seat.id) -> {
                            setBackgroundColor(seatColor(R.color.nc_seat_booked))
                            setTextColor(seatColor(R.color.nc_text_primary))
                        }
                        else -> {
                            setBackgroundColor(seatColor(R.color.nc_seat_available))
                            setTextColor(seatColor(R.color.nc_text_primary))
                        }

                    }
                }

                rowLayout.addView(seatButton)
            }

            seatGridContainer.addView(rowTitle)
            seatGridContainer.addView(rowLayout)
        }
    }

    private fun extractSeatNumber(seatLabel: String): Int {
        return seatLabel.drop(1).toIntOrNull() ?: 0
    }

    private fun seatColor(colorResId: Int): Int {
        return ContextCompat.getColor(this, colorResId)
    }
}