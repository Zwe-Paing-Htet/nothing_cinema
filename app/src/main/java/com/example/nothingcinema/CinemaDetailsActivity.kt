package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CinemaDetailsActivity : AppCompatActivity() {

    private lateinit var cinemaNameTextView: TextView
    private lateinit var cinemaTownshipTextView: TextView
    private lateinit var cinemaFullAddressTextView: TextView
    private lateinit var cinemaPosterImageView: ImageView
    private lateinit var openMapButton: Button
    private lateinit var theatreContainer: LinearLayout
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_details)

        cinemaNameTextView = findViewById(R.id.cinemaNameTextView)
        cinemaTownshipTextView = findViewById(R.id.cinemaTownshipTextView)
        cinemaFullAddressTextView = findViewById(R.id.cinemaFullAddressTextView)
        cinemaPosterImageView = findViewById(R.id.cinemaPosterImageView)
        openMapButton = findViewById(R.id.openMapButton)
        theatreContainer = findViewById(R.id.theatreContainer)
        backButton = findViewById(R.id.backButton)

        dbHelper = DatabaseHelper(this)

        backButton.setOnClickListener {
            finish()
        }

        val cinemaId = intent.getLongExtra("cinema_id", -1)

        if (cinemaId != -1L) {
            val cinema = dbHelper.getCinemaById(cinemaId)

            if (cinema != null) {
                cinemaNameTextView.text = cinema.name
                cinemaTownshipTextView.text = cinema.township
                cinemaFullAddressTextView.text = cinema.fullAddress

                cinema.image?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    cinemaPosterImageView.setImageBitmap(bitmap)
                }

                openMapButton.setOnClickListener {
                    val googleMapUrl = cinema.googleMapUrl
                    if (!googleMapUrl.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapUrl))
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No Google Map link available", Toast.LENGTH_SHORT).show()
                    }
                }

                val theatreList = dbHelper.getTheatresByCinemaId(cinemaId)

                theatreContainer.removeAllViews()

                for (theatre in theatreList) {
                    val theatreView = layoutInflater.inflate(
                        R.layout.item_theatre_collection,
                        theatreContainer,
                        false
                    )

                    val theatreImage = theatreView.findViewById<ImageView>(R.id.theatreImageView)
                    val theatreName = theatreView.findViewById<TextView>(R.id.theatreNameTextView)
                    val theatreType = theatreView.findViewById<TextView>(R.id.theatreTypeTextView)

                    theatreName.text = theatre.name
                    theatreType.text = theatre.type ?: "Standard"

                    theatre.image?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        theatreImage.setImageBitmap(bitmap)
                    } ?: run {
                        theatreImage.setImageResource(R.drawable.ic_placeholder)
                    }

                    theatreView.setOnClickListener {
                        val intent = Intent(this, TheatreDetailsActivity::class.java)
                        intent.putExtra("theatre_id", theatre.id)
                        startActivity(intent)
                    }

                    theatreContainer.addView(theatreView)
                }
            }
        }
    }
}