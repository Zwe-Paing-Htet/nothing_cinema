package com.example.nothingcinema

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.View

class ViewAdminActivity : AppCompatActivity() {

    private lateinit var recyclerViewAdmins: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adminAdapter: AdminAdapter
    private lateinit var adminList: List<DatabaseHelper.User>
    private lateinit var backButton: ImageButton

    private lateinit var emptyAdminsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_admin)

        recyclerViewAdmins = findViewById(R.id.recyclerViewAdmins)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        emptyAdminsTextView = findViewById(R.id.emptyAdminsTextView)

        dbHelper = DatabaseHelper(this)

        loadAdmins()
    }

//    private fun loadAdmins() {
//        adminList = dbHelper.getAllAdmins()
//
//        if (adminList.isEmpty()) {
//            Toast.makeText(this, "No admins found", Toast.LENGTH_SHORT).show()
//        }
//
//        adminAdapter = AdminAdapter(adminList)
//        recyclerViewAdmins.layoutManager = LinearLayoutManager(this)
//        recyclerViewAdmins.adapter = adminAdapter
//    }

    private fun loadAdmins() {
        adminList = dbHelper.getAllAdmins()

        if (adminList.isEmpty()) {
            emptyAdminsTextView.visibility = View.VISIBLE
            recyclerViewAdmins.visibility = View.GONE
        } else {
            emptyAdminsTextView.visibility = View.GONE
            recyclerViewAdmins.visibility = View.VISIBLE
        }

        adminAdapter = AdminAdapter(adminList)
        recyclerViewAdmins.layoutManager = LinearLayoutManager(this)
        recyclerViewAdmins.adapter = adminAdapter
    }
}