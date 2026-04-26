package com.example.nothingcinema

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDataChartActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_data_chart)

        dbHelper = DatabaseHelper(this)

        setupBottomNav()
        loadStatCards()
        loadTopMovies()
        loadMonthlyBookingsChart()
        loadTheatrePieChart()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_admin_chart

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_admin_dashboard -> {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_admin_chart -> true
                else -> false
            }
        }
    }

    private fun loadStatCards() {
        findViewById<TextView>(R.id.totalMoviesValue).text = dbHelper.getTotalMoviesCount().toString()
        findViewById<TextView>(R.id.totalCinemasValue).text = dbHelper.getTotalCinemasCount().toString()
        findViewById<TextView>(R.id.totalTheatresValue).text = dbHelper.getTotalTheatresCount().toString()
        findViewById<TextView>(R.id.totalUsersValue).text = dbHelper.getTotalUsersCount().toString()
        findViewById<TextView>(R.id.totalBookingsValue).text = dbHelper.getTotalBookingsCount().toString()
    }

    private fun loadTopMovies() {
        val topMovies = dbHelper.getTop3SellingMovies()

        val first = findViewById<TextView>(R.id.topMovie1Text)
        val second = findViewById<TextView>(R.id.topMovie2Text)
        val third = findViewById<TextView>(R.id.topMovie3Text)

        val views = listOf(first, second, third)

        for (i in views.indices) {
            if (i < topMovies.size) {
                val item = topMovies[i]
                views[i].text = "${i + 1}. ${item.movieName} — ${item.bookingCount} bookings"
            } else {
                views[i].text = "${i + 1}. No data yet"
            }
        }
    }

    private fun loadMonthlyBookingsChart() {
        val barChart = findViewById<BarChart>(R.id.monthlyBookingsBarChart)
        val monthlyStats = dbHelper.getMonthlyBookingStats()

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        monthlyStats.forEachIndexed { index, stat ->
            entries.add(BarEntry(index.toFloat(), stat.bookingCount.toFloat()))
            labels.add(stat.monthLabel)
        }

        val dataSet = BarDataSet(entries, "Bookings")
        val barData = BarData(dataSet)
        barData.barWidth = 0.55f

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.setFitBars(true)

        val desc = Description()
        desc.text = ""
        barChart.description = desc

        barChart.invalidate()
    }

    private fun loadTheatrePieChart() {
        val pieChart = findViewById<PieChart>(R.id.theatrePieChart)
        val theatreStats = dbHelper.getTheatreBookingStats()

        val entries = theatreStats.map {
            PieEntry(it.bookingCount.toFloat(), it.theatreName)
        }

        val dataSet = PieDataSet(entries, "Theatres")
        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.centerText = "Most Booked\nTheatres"
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.legend.isEnabled = true

        val desc = Description()
        desc.text = ""
        pieChart.description = desc

        pieChart.invalidate()
    }
}