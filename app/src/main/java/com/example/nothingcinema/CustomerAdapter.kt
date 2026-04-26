package com.example.nothingcinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomerAdapter(
    private val customerList: List<DatabaseHelper.User>
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerId: TextView = itemView.findViewById(R.id.tvCustomerId)
        val tvCustomerUsername: TextView = itemView.findViewById(R.id.tvCustomerUsername)
        val tvCustomerEmail: TextView = itemView.findViewById(R.id.tvCustomerEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customerList[position]
//
//        holder.tvCustomerId.text = "ID: ${customer.id}"
//        holder.tvCustomerUsername.text = "Username: ${customer.username}"
//        holder.tvCustomerEmail.text = "Email: ${customer.email}"

        holder.tvCustomerId.text = "ID • ${customer.id}"
        holder.tvCustomerUsername.text = customer.username
        holder.tvCustomerEmail.text = customer.email
    }

    override fun getItemCount(): Int {
        return customerList.size
    }
}