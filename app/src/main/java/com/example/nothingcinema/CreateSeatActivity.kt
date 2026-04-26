package com.example.nothingcinema

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateSeatActivity : AppCompatActivity() {

    private lateinit var theatreSpinner: Spinner
    private lateinit var seatTypeSpinner: Spinner
    private lateinit var seatRowSpinner: Spinner
    private lateinit var seatCountSpinner: Spinner
    private lateinit var createSeatButton: Button
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var backButton: ImageButton


    private lateinit var theatreList: List<DatabaseHelper.Theatre>
    private lateinit var seatTypeList: List<DatabaseHelper.SeatType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_seat)

        theatreSpinner = findViewById(R.id.theatreSpinner)
        seatTypeSpinner = findViewById(R.id.seatTypeSpinner)
        seatRowSpinner = findViewById(R.id.seatRowSpinner)
        seatCountSpinner = findViewById(R.id.seatCountSpinner)
        createSeatButton = findViewById(R.id.createSeatButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        loadTheatres()
        loadSeatTypes()
        loadSeatRows()
        loadSeatCounts()

        createSeatButton.setOnClickListener {
            if (theatreList.isEmpty() || seatTypeList.isEmpty()) {
                Toast.makeText(this, "Theatre or seat type data is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedTheatre = theatreList[theatreSpinner.selectedItemPosition]
            val selectedSeatType = seatTypeList[seatTypeSpinner.selectedItemPosition]
            val selectedSeatRow = seatRowSpinner.selectedItem.toString()
            val selectedSeatCount = seatCountSpinner.selectedItem.toString().toInt()

            val rowAlreadyUsed = dbHelper.isSeatRowUsedInTheatre(selectedTheatre.id, selectedSeatRow)

            if (rowAlreadyUsed) {
                Toast.makeText(
                    this,
                    "Seat row $selectedSeatRow is already used in ${selectedTheatre.name}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertSeatRow(
                seatRow = selectedSeatRow,
                seatCount = selectedSeatCount,
                theatreId = selectedTheatre.id,
                seatTypeId = selectedSeatType.id
            )

            if (success) {
                Toast.makeText(
                    this,
                    "Seats $selectedSeatRow" + "1 to $selectedSeatRow" + selectedSeatCount + " created successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Failed to create seat row", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTheatres() {
        theatreList = dbHelper.getAllTheatres()
        val theatreNames = theatreList.map { it.name }
        val adapter = ArrayAdapter(this, R.layout.item_spinner_selected, theatreNames)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        theatreSpinner.adapter = adapter
    }

    private fun loadSeatTypes() {
        seatTypeList = dbHelper.getAllSeatTypes()
        val seatTypeNames = seatTypeList.map { "${it.name} - ${it.price}" }
        val adapter = ArrayAdapter(this, R.layout.item_spinner_selected, seatTypeNames)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        seatTypeSpinner.adapter = adapter
    }

    private fun loadSeatRows() {
        val seatRows = listOf("A", "B", "C", "D", "E")
        val adapter = ArrayAdapter(this, R.layout.item_spinner_selected, seatRows)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        seatRowSpinner.adapter = adapter
    }

    private fun loadSeatCounts() {
        val seatCounts = (1..10).map { it.toString() }
        val adapter = ArrayAdapter(this, R.layout.item_spinner_selected, seatCounts)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        seatCountSpinner.adapter = adapter
    }
}