package com.example.nothingcinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminAdapter(
    private val adminList: List<DatabaseHelper.User>
) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAdminId: TextView = itemView.findViewById(R.id.tvAdminId)
        val tvAdminUsername: TextView = itemView.findViewById(R.id.tvAdminUsername)
        val tvAdminEmail: TextView = itemView.findViewById(R.id.tvAdminEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val admin = adminList[position]

//        holder.tvAdminId.text = "ID: ${admin.id}"
//        holder.tvAdminUsername.text = "Username: ${admin.username}"
//        holder.tvAdminEmail.text = "Email: ${admin.email}"

        holder.tvAdminId.text = "ID • ${admin.id}"
        holder.tvAdminUsername.text = admin.username
        holder.tvAdminEmail.text = admin.email
    }

    override fun getItemCount(): Int {
        return adminList.size
    }
}