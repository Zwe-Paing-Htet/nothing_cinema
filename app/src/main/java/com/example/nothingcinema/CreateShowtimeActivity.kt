package com.example.nothingcinema

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class CreateShowtimeActivity : AppCompatActivity() {

    private lateinit var theatreSpinner: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var saveShowtimeButton: Button
    private lateinit var movieNameTextView: TextView


    private lateinit var backButton: ImageButton

    private lateinit var dbHelper: DatabaseHelper

    private var selectedTheatreId: Long = -1
    private var movieId: Long = -1
    private var movieName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_showtime)

        // Initialize UI elements
        theatreSpinner = findViewById(R.id.theatreSpinner)
        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        saveShowtimeButton = findViewById(R.id.saveShowtimeButton)
        movieNameTextView = findViewById(R.id.movieNameTextView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get movie ID and movie name passed from the previous activity
        movieId = intent.getLongExtra("movie_id", -1)
        movieName = intent.getStringExtra("movie_name")

        // Check if movieId and movieName are valid
        if (movieId == -1L || movieName.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid movie data", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set the Movie Name in the TextView dynamically
//        movieNameTextView.text = "Add Showtime for: $movieName"
        movieNameTextView.text = movieName ?: "Unknown Movie"

        // Populate the theatre spinner with available theatres
        val theatreList = dbHelper.getAllTheatres()
        val theatreNames = theatreList.map { it.name }
        val theatreAdapter = ArrayAdapter(this, R.layout.item_spinner_selected, theatreNames)
        theatreAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        theatreSpinner.adapter = theatreAdapter

        // Set the listener for the spinner to get the selected theatre ID
        theatreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedView: View?, position: Int, id: Long) {
                selectedTheatreId = theatreList[position].id
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing
            }
        }

        // Handle the save showtime button click
        saveShowtimeButton.setOnClickListener {
            // Get the selected date
            val showtimeDate = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"

            // Get the selected time
            val showtimeTime = getFormattedTime(timePicker)

            // Validate inputs
            if (selectedTheatreId != -1L && !TextUtils.isEmpty(showtimeDate) && !TextUtils.isEmpty(showtimeTime)) {
                // Insert the showtime into the database
                val success = dbHelper.insertShowtime(showtimeDate, showtimeTime, selectedTheatreId, movieId)

                if (success != -1L) {
                    Toast.makeText(this, "Showtime added successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Go back to the previous screen
                } else {
                    Toast.makeText(this, "Failed to add showtime", Toast.LENGTH_SHORT).show()
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