package com.example.nothingcinema

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class TheatreSeatStructureActivity : AppCompatActivity() {

    private lateinit var theatreInfoTextView: TextView
    private lateinit var totalSeatsTextView: TextView
    private lateinit var seatStructureContainer: LinearLayout
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var backButton: ImageButton
    private var currentTheatreId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theatre_seat_structure)

        theatreInfoTextView = findViewById(R.id.theatreInfoTextView)
        totalSeatsTextView = findViewById(R.id.totalSeatsTextView)
        seatStructureContainer = findViewById(R.id.seatStructureContainer)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
        dbHelper = DatabaseHelper(this)

        currentTheatreId = intent.getLongExtra("theatre_id", -1L)
        if (currentTheatreId != -1L) {
            loadTheatreSeatStructure(currentTheatreId)
        }
    }

    private fun loadTheatreSeatStructure(theatreId: Long) {
        val theatre = dbHelper.getTheatreById(theatreId)
        val seats = dbHelper.getSeatsByTheatreId(theatreId)
        val totalSeats = dbHelper.getSeatCountByTheatreId(theatreId)

//        theatreInfoTextView.text = buildString {
//            append("Theatre: ${theatre?.name ?: "Unknown Theatre"}\n")
//            append("Type: ${theatre?.type ?: "Unknown Type"}")
//        }
//
//        totalSeatsTextView.text = "Total Seats: $totalSeats"

        theatreInfoTextView.text = buildString {
            append("${theatre?.name ?: "Unknown Theatre"}\n")
            append(theatre?.type ?: "Unknown Type")
        }

        totalSeatsTextView.text = "Total Seats • $totalSeats"

        seatStructureContainer.removeAllViews()

        val groupedByRow = seats.groupBy { seat ->
            seat.seatLabel.firstOrNull()?.toString() ?: "Unknown"
        }.toSortedMap()

        for ((rowName, seatList) in groupedByRow) {
            val sortedSeatList = seatList.sortedBy { extractSeatNumber(it.seatLabel) }
            val firstSeat = sortedSeatList.firstOrNull()
            val seatType = firstSeat?.let { dbHelper.getSeatTypeById(it.seatTypeId) }

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background =
                    ContextCompat.getDrawable(this@TheatreSeatStructureActivity, R.drawable.bg_card)
                setPadding(32, 32, 32, 32)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            card.layoutParams = params

            val rowTitle = TextView(this).apply {
                text = "Row $rowName"
                textSize = 22f
                setTextColor(
                    ContextCompat.getColor(
                        this@TheatreSeatStructureActivity,
                        R.color.nc_text_primary
                    )
                )
                setPadding(0, 0, 0, 10)
            }

            val seatTypeText = TextView(this).apply {
                text = seatType?.name ?: "Unknown Seat Type"
                textSize = 16f
                setTextColor(
                    ContextCompat.getColor(
                        this@TheatreSeatStructureActivity,
                        R.color.nc_text_secondary
                    )
                )
                setPadding(0, 0, 0, 6)
            }

            val seatPriceText = TextView(this).apply {
                text = "%.2f MMK".format(seatType?.price ?: 0.0)
                textSize = 14f
                setTextColor(
                    ContextCompat.getColor(
                        this@TheatreSeatStructureActivity,
                        R.color.nc_text_secondary
                    )
                )
                setPadding(0, 0, 0, 10)
            }

            val rowSeats = TextView(this).apply {
                text = sortedSeatList.joinToString(" ") { it.seatLabel }
                textSize = 16f
                setTextColor(
                    ContextCompat.getColor(
                        this@TheatreSeatStructureActivity,
                        R.color.nc_text_primary
                    )
                )
                setPadding(0, 0, 0, 14)
            }

            val deleteRowButton = Button(this).apply {
                text = "Delete Row $rowName"
                background = ContextCompat.getDrawable(
                    this@TheatreSeatStructureActivity,
                    R.drawable.bg_button_danger_outline
                )
                setTextColor(
                    ContextCompat.getColor(
                        this@TheatreSeatStructureActivity,
                        R.color.nc_accent_red
                    )
                )
                textSize = 15f
                isAllCaps = false
                setOnClickListener {
                    showDeleteRowConfirmation(theatreId, rowName)
                }
            }

            card.addView(rowTitle)
            card.addView(seatTypeText)
            card.addView(seatPriceText)
            card.addView(rowSeats)
            card.addView(deleteRowButton)

            seatStructureContainer.addView(card)

        }
    }

    private fun showDeleteRowConfirmation(theatreId: Long, rowName: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Row $rowName")
            .setMessage("Are you sure you want to delete all seats in row $rowName?")
            .setPositiveButton("Yes") { _, _ ->
                val deleted = dbHelper.deleteSeatRow(theatreId, rowName)
                if (deleted) {
                    Toast.makeText(this, "Row $rowName deleted successfully", Toast.LENGTH_SHORT).show()
                    loadTheatreSeatStructure(theatreId)
                } else {
                    Toast.makeText(this, "Failed to delete row $rowName", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun extractSeatNumber(seatLabel: String): Int {
        return seatLabel.drop(1).toIntOrNull() ?: 0
    }
}