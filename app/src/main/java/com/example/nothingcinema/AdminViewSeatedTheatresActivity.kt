package com.example.nothingcinema

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class AdminViewSeatedTheatresActivity : AppCompatActivity() {

    private lateinit var seatedTheatresRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var seatedTheatreAdapter: AdminSeatedTheatreAdapter
    private lateinit var backButton: ImageButton
    private lateinit var emptySeatedTheatresTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_seated_theatres)

        seatedTheatresRecyclerView = findViewById(R.id.seatedTheatresRecyclerView)
        backButton = findViewById(R.id.backButton)
        emptySeatedTheatresTextView = findViewById(R.id.emptySeatedTheatresTextView)

        backButton.setOnClickListener {
            finish()
        }
        dbHelper = DatabaseHelper(this)

        loadSeatedTheatres()
    }

//    private fun loadSeatedTheatres() {
//        val theatreList = dbHelper.getTheatresWithSeats()
//        seatedTheatreAdapter = AdminSeatedTheatreAdapter(theatreList)
//        seatedTheatresRecyclerView.layoutManager = LinearLayoutManager(this)
//        seatedTheatresRecyclerView.adapter = seatedTheatreAdapter
//    }
    private fun loadSeatedTheatres() {
        val theatreList = dbHelper.getTheatresWithSeats()

        if (theatreList.isEmpty()) {
            emptySeatedTheatresTextView.visibility = View.VISIBLE
            seatedTheatresRecyclerView.visibility = View.GONE
        } else {
            emptySeatedTheatresTextView.visibility = View.GONE
            seatedTheatresRecyclerView.visibility = View.VISIBLE
        }

        seatedTheatreAdapter = AdminSeatedTheatreAdapter(theatreList)
        seatedTheatresRecyclerView.layoutManager = LinearLayoutManager(this)
        seatedTheatresRecyclerView.adapter = seatedTheatreAdapter
    }
}