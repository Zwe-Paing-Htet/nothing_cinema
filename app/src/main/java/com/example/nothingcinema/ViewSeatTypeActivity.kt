package com.example.nothingcinema

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewSeatTypeActivity : AppCompatActivity() {

    private lateinit var seatTypeRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var seatTypeAdapter: SeatTypeAdapter

    private lateinit var backButton: ImageButton
    private lateinit var emptySeatTypesTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_seat_type)

        seatTypeRecyclerView = findViewById(R.id.seatTypeRecyclerView)
        backButton = findViewById(R.id.backButton)
        emptySeatTypesTextView = findViewById(R.id.emptySeatTypesTextView)

        backButton.setOnClickListener {
            finish()
        }
        dbHelper = DatabaseHelper(this)

        loadSeatTypes()
    }

    override fun onResume() {
        super.onResume()
        loadSeatTypes()
    }
//
//    private fun loadSeatTypes() {
//        val seatTypeList = dbHelper.getAllSeatTypes()
//
//        if (seatTypeList.isEmpty()) {
//            Toast.makeText(this, "No seat types found", Toast.LENGTH_SHORT).show()
//        }
//
//        seatTypeAdapter = SeatTypeAdapter(seatTypeList, dbHelper) {
//            loadSeatTypes()
//        }
//
//        seatTypeRecyclerView.layoutManager = LinearLayoutManager(this)
//        seatTypeRecyclerView.adapter = seatTypeAdapter
//    }
    private fun loadSeatTypes() {
        val seatTypeList = dbHelper.getAllSeatTypes()

        if (seatTypeList.isEmpty()) {
            emptySeatTypesTextView.visibility = View.VISIBLE
            seatTypeRecyclerView.visibility = View.GONE
        } else {
            emptySeatTypesTextView.visibility = View.GONE
            seatTypeRecyclerView.visibility = View.VISIBLE
        }

        seatTypeAdapter = SeatTypeAdapter(seatTypeList, dbHelper) {
            loadSeatTypes()
        }

        seatTypeRecyclerView.layoutManager = LinearLayoutManager(this)
        seatTypeRecyclerView.adapter = seatTypeAdapter
    }
}