package com.example.nothingcinema

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.View

class ViewCustomerActivity : AppCompatActivity() {

    private lateinit var recyclerViewCustomers: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var customerList: List<DatabaseHelper.User>
    private lateinit var backButton: ImageButton

    private lateinit var emptyCustomersTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_customer)

        recyclerViewCustomers = findViewById(R.id.recyclerViewCustomers)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        emptyCustomersTextView = findViewById(R.id.emptyCustomersTextView)

        dbHelper = DatabaseHelper(this)

        loadCustomers()
    }

//    private fun loadCustomers() {
//        customerList = dbHelper.getAllCustomers()
//
//        if (customerList.isEmpty()) {
//            Toast.makeText(this, "No customers found", Toast.LENGTH_SHORT).show()
//        }
//
//        customerAdapter = CustomerAdapter(customerList)
//        recyclerViewCustomers.layoutManager = LinearLayoutManager(this)
//        recyclerViewCustomers.adapter = customerAdapter
//    }

    private fun loadCustomers() {
        customerList = dbHelper.getAllCustomers()

        if (customerList.isEmpty()) {
            emptyCustomersTextView.visibility = View.VISIBLE
            recyclerViewCustomers.visibility = View.GONE
        } else {
            emptyCustomersTextView.visibility = View.GONE
            recyclerViewCustomers.visibility = View.VISIBLE
        }

        customerAdapter = CustomerAdapter(customerList)
        recyclerViewCustomers.layoutManager = LinearLayoutManager(this)
        recyclerViewCustomers.adapter = customerAdapter
    }
}