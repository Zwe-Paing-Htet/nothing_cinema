package com.example.nothingcinema

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException

class EditShowtimeActivity : AppCompatActivity() {

    private lateinit var movieNameTextView: TextView
    private lateinit var theatreNameTextView: TextView
    private lateinit var saveShowtimeButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker


    private lateinit var backButton: ImageButton


    private var showtimeId: Long = -1
    private var movieId: Long = -1
    private var theatreId: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_showtime)

        // Initialize UI elements
        movieNameTextView = findViewById(R.id.movieNameTextView)
        theatreNameTextView = findViewById(R.id.theatreNameTextView)
        saveShowtimeButton = findViewById(R.id.saveShowtimeButton)
        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get the showtime ID passed from the previous activity
        showtimeId = intent.getLongExtra("showtime_id", -1)

        if (showtimeId != -1L) {
            val showtime = dbHelper.getShowtimeById(showtimeId)

            if (showtime != null) {
                // Populate the views with existing showtime details
                val calendar = Calendar.getInstance()
                val dateParts = showtime.showtimeDate.split("/")
                calendar.set(Calendar.YEAR, dateParts[2].toInt())
                calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
                calendar.set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())

                // Set the date on the DatePicker
                datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                // Set the time on the TimePicker
//                val timeParts = showtime.showtimeTime.split(":")
//                val hour = timeParts[0].toInt()
//                val minute = timeParts[1].substring(0, 2).toInt() // Removing AM/PM part if present
//                timePicker.setHour(hour)
//                timePicker.setMinute(minute)

                try {
                    val parser = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val parsedTime = parser.parse(showtime.showtimeTime)

                    if (parsedTime != null) {
                        val calendar = Calendar.getInstance()
                        calendar.time = parsedTime
                        timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
                        timePicker.minute = calendar.get(Calendar.MINUTE)
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    timePicker.hour = 12
                    timePicker.minute = 0
                }

                // Fetch the movie and theatre details
                val movie = dbHelper.getMovieById(showtime.movieId)
                val theatre = dbHelper.getTheatreById(showtime.theatreId)

                // Set the movie and theatre details to TextViews (non-editable)
//                movieNameTextView.text = "Movie: ${movie?.name ?: "Unknown Movie"}"
//                theatreNameTextView.text = "Theatre: ${theatre?.name ?: "Unknown Theatre"}"

                movieNameTextView.text = movie?.name ?: "Unknown Movie"
                theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"

                // Save the movieId and theatreId for use in the database update
                movieId = showtime.movieId
                theatreId = showtime.theatreId
            }
        }

        // Save the updated showtime when the "Save" button is clicked
        saveShowtimeButton.setOnClickListener {
            // Get the selected date and time
            val showtimeDate = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"
            val showtimeTime = getFormattedTime(timePicker)

                if (movieId != -1L && theatreId != -1L && !TextUtils.isEmpty(showtimeDate) && !TextUtils.isEmpty(showtimeTime)) {
                    val success = dbHelper.updateShowtime(showtimeId, showtimeDate, showtimeTime, theatreId)


                    if (success > 0) {
                    Toast.makeText(this, "Showtime updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()  // Go back to the previous screen
                } else {
                    Toast.makeText(this, "Failed to update showtime", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to format the selected time from TimePicker
    private fun getFormattedTime(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())  // 12-hour format
        return timeFormat.format(calendar.time)
    }
}