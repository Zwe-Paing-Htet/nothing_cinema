package com.example.nothingcinema

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var loggedInUserTextView: TextView
    private lateinit var btnProfile: Button
    private lateinit var logoutButton: Button
    private lateinit var themeSettingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        loggedInUserTextView = findViewById(R.id.loggedInUserTextView)
        btnProfile = findViewById(R.id.btnProfile)
        logoutButton = findViewById(R.id.logoutButton)
        themeSettingsButton = findViewById(R.id.themeSettingsButton)

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Admin")
        loggedInUserTextView.text = "Logged in as $username"

        val createMovieButton: Button = findViewById(R.id.createMovieButton)
        val viewMovieButton: Button = findViewById(R.id.viewMovieButton)
        val createShowtimeButton: Button = findViewById(R.id.createShowtimeButton)
        val viewShowtimeButton: Button = findViewById(R.id.viewShowtimeButton)

        val createCinemaButton: Button = findViewById(R.id.createCinemaButton)
        val viewCinemaButton: Button = findViewById(R.id.viewCinemaButton)
        val createTheatreButton: Button = findViewById(R.id.createTheatreButton)
        val viewTheatreButton: Button = findViewById(R.id.viewTheatreButton)

        val viewBookingButton: Button = findViewById(R.id.viewBookingsButton)
        val btnViewCustomers: Button = findViewById(R.id.btnViewCustomers)
        val registerAdminButton: Button = findViewById(R.id.registerAdminButton)
        val btnViewAdmins: Button = findViewById(R.id.btnViewAdmins)
        val insertSeatTypePageButton: Button = findViewById(R.id.insertSeatTypePageButton)
        val viewSeatTypePageButton: Button = findViewById(R.id.viewSeatTypePageButton)
        val createSeatPageButton: Button = findViewById(R.id.createSeatPageButton)
        val viewSeatPageButton: Button = findViewById(R.id.viewSeatPageButton)
        val viewSeatStatusPageButton: Button = findViewById(R.id.viewSeatStatusPageButton)
        val reviewBuyPaymentsButton: Button = findViewById(R.id.reviewBuyPaymentsButton)

        btnProfile.setOnClickListener {
            startActivity(Intent(this, CustomerProfileActivity::class.java))
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        createMovieButton.setOnClickListener {
            startActivity(Intent(this, CreateMovieActivity::class.java))
        }

        viewMovieButton.setOnClickListener {
            startActivity(Intent(this, ViewMovieActivity::class.java))
        }

        createShowtimeButton.setOnClickListener {
            startActivity(Intent(this, ViewMovieActivity::class.java))
        }

        viewShowtimeButton.setOnClickListener {
            startActivity(Intent(this, ViewShowtimeActivity::class.java))
        }

        createCinemaButton.setOnClickListener {
            startActivity(Intent(this, CreateCinemaActivity::class.java))
        }

        viewCinemaButton.setOnClickListener {
            startActivity(Intent(this, ViewCinemaActivity::class.java))
        }

        createTheatreButton.setOnClickListener {
            startActivity(Intent(this, ViewCinemaActivity::class.java))
        }

        viewTheatreButton.setOnClickListener {
            startActivity(Intent(this, ViewTheatreActivity::class.java))
        }

        viewBookingButton.setOnClickListener {
            startActivity(Intent(this, ViewBookingActivity::class.java))
        }

        btnViewCustomers.setOnClickListener {
            startActivity(Intent(this, ViewCustomerActivity::class.java))
        }

        registerAdminButton.setOnClickListener {
            startActivity(Intent(this, RegisterAdminActivity::class.java))
        }

        btnViewAdmins.setOnClickListener {
            startActivity(Intent(this, ViewAdminActivity::class.java))
        }

        insertSeatTypePageButton.setOnClickListener {
            startActivity(Intent(this, CreateSeatTypeActivity::class.java))
        }

        viewSeatTypePageButton.setOnClickListener {
            startActivity(Intent(this, ViewSeatTypeActivity::class.java))
        }

        createSeatPageButton.setOnClickListener {
            startActivity(Intent(this, CreateSeatActivity::class.java))
        }

        viewSeatPageButton.setOnClickListener {
            startActivity(Intent(this, ViewSeatActivity::class.java))
        }

        viewSeatStatusPageButton.setOnClickListener {
            startActivity(Intent(this, AdminViewSeatedTheatresActivity::class.java))
        }

        reviewBuyPaymentsButton.setOnClickListener {
            startActivity(Intent(this, ReviewBuyPaymentsActivity::class.java))
        }

        themeSettingsButton.setOnClickListener {
            startActivity(Intent(this, ThemeSettingsActivity::class.java))
        }

        setStyledButtonWithBottomLeftImage(
            button = createMovieButton,
            title = "Create Movie",
            subtitle = "Admin",
            imageRes = R.drawable.ic_movie_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewMovieButton,
            title = "View Movies",
            subtitle = "Admin",
            imageRes = R.drawable.ic_movie_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = createShowtimeButton,
            title = "Create Showtime",
            subtitle = "Admin",
            imageRes = R.drawable.ic_showtime_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewShowtimeButton,
            title = "View Showtimes",
            subtitle = "Admin",
            imageRes = R.drawable.ic_showtime_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = createCinemaButton,
            title = "Create Cinema",
            subtitle = "Admin",
            imageRes = R.drawable.ic_cinema_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewCinemaButton,
            title = "View Cinemas",
            subtitle = "Admin",
            imageRes = R.drawable.ic_cinema_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = createTheatreButton,
            title = "Create Theatre",
            subtitle = "Admin",
            imageRes = R.drawable.ic_theatre_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewTheatreButton,
            title = "View Theatres",
            subtitle = "Admin",
            imageRes = R.drawable.ic_theatre_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = insertSeatTypePageButton,
            title = "Insert Seat Type",
            subtitle = "Admin",
            imageRes = R.drawable.ic_seat_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewSeatTypePageButton,
            title = "View Seat Types",
            subtitle = "Admin",
            imageRes = R.drawable.ic_seat_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = createSeatPageButton,
            title = "Create Seat",
            subtitle = "Admin",
            imageRes = R.drawable.ic_seat_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewSeatPageButton,
            title = "View Seats",
            subtitle = "Admin",
            imageRes = R.drawable.ic_seat_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewSeatStatusPageButton,
            title = "View Seat Status",
            subtitle = "Admin",
            imageRes = R.drawable.ic_seat_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = btnViewCustomers,
            title = "View Customers",
            subtitle = "Admin",
            imageRes = R.drawable.ic_user_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = registerAdminButton,
            title = "Register Admin",
            subtitle = "Admin",
            imageRes = R.drawable.ic_user_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = btnViewAdmins,
            title = "View Admins",
            subtitle = "Admin",
            imageRes = R.drawable.ic_user_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = viewBookingButton,
            title = "View Bookings",
            subtitle = "Admin",
            imageRes = R.drawable.ic_ticket_admin
        )

        setStyledButtonWithBottomLeftImage(
            button = reviewBuyPaymentsButton,
            title = "Review Payments",
            subtitle = "Admin",
            imageRes = R.drawable.ic_ticket_admin
        )

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNav.selectedItemId = R.id.nav_admin_dashboard

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_admin_dashboard -> true
                R.id.nav_admin_chart -> {
                    startActivity(Intent(this, AdminDataChartActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun setStyledButtonWithBottomLeftImage(
        button: Button,
        title: String,
        subtitle: String,
        imageRes: Int
    ) {
        val drawable = ContextCompat.getDrawable(this, imageRes)?.mutate()
        drawable?.setBounds(0, 0, 45.dpToPx(), 45.dpToPx())

        val builder = SpannableStringBuilder()

        builder.append(title)
        builder.append("\n")

        val subtitleStart = builder.length
        builder.append(subtitle)
        val subtitleEnd = builder.length
        builder.setSpan(
            ForegroundColorSpan(Color.parseColor("#222222")),
            subtitleStart,
            subtitleEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        builder.append("\n\n\n")

        val imageStart = builder.length
        builder.append(" ")
        val imageEnd = builder.length

        if (drawable != null) {
            builder.setSpan(
                ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM),
                imageStart,
                imageEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        button.text = builder
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
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