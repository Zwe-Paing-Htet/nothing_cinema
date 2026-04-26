package com.example.nothingcinema

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateSeatTypeActivity : AppCompatActivity() {

    private lateinit var seatTypeNameEditText: EditText
    private lateinit var seatPriceEditText: EditText
    private lateinit var insertSeatTypeButton: Button
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_seat_type)

        seatTypeNameEditText = findViewById(R.id.seatTypeNameEditText)
        seatPriceEditText = findViewById(R.id.seatPriceEditText)
        insertSeatTypeButton = findViewById(R.id.insertSeatTypeButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        insertSeatTypeButton.setOnClickListener {
            val seatTypeName = seatTypeNameEditText.text.toString().trim()
            val seatPriceText = seatPriceEditText.text.toString().trim()

            if (TextUtils.isEmpty(seatTypeName) || TextUtils.isEmpty(seatPriceText)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val seatPrice = seatPriceText.toDoubleOrNull()
            if (seatPrice == null) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.insertSeatType(seatTypeName, seatPrice)

            if (result != -1L) {
                Toast.makeText(this, "Seat type inserted successfully", Toast.LENGTH_SHORT).show()
                seatTypeNameEditText.text.clear()
                seatPriceEditText.text.clear()
            } else {
                Toast.makeText(this, "Failed to insert seat type", Toast.LENGTH_SHORT).show()
            }
        }
    }
}