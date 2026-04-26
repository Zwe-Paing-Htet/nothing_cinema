package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TheatreAdapterForView(
    private val theatreList: MutableList<DatabaseHelper.Theatre>,
    private val onTheatreClick: (DatabaseHelper.Theatre) -> Unit // Lambda for theatre detail page navigation
) : RecyclerView.Adapter<TheatreAdapterForView.TheatreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheatreViewHolder {
        // Inflate the custom item layout for Theatre (item_theatre.xml)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_theatre, parent, false)
        return TheatreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TheatreViewHolder, position: Int) {
        val theatre = theatreList[position]

        // Bind data to the views
//        holder.theatreNameTextView.text = theatre.name
//        holder.theatreTypeTextView.text = "Type: ${theatre.type}"

        holder.theatreNameTextView.text = theatre.name
        holder.theatreTypeTextView.text = theatre.type ?: "Standard"

        // Set the theatre image (if available)
        theatre.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.theatreImageView.setImageBitmap(bitmap)
        }

        // Set click listener for the theatre item (Theatre Details Page)
        holder.itemView.setOnClickListener {
            onTheatreClick(theatre)
        }

        // Set click listener for the "Edit Theatre" button
        holder.editTheatreButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditTheatreActivity::class.java).apply {
                putExtra("theatre_id", theatre.id)
                putExtra("theatre_name", theatre.name)
                putExtra("theatre_type", theatre.type)
                putExtra("theatre_image", theatre.image) // Pass the theatre image (byte array)
            }
            context.startActivity(intent)
        }

        // Set click listener for the "Delete Theatre" button
        holder.deleteTheatreButton.setOnClickListener {
            val dbHelper = DatabaseHelper(holder.itemView.context)
            val deletedRows = dbHelper.deleteTheatre(theatre.id)

            // If deletion was successful, remove the theatre from the list and notify the adapter
            if (deletedRows > 0) {
                theatreList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(holder.itemView.context, "Theatre deleted successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Failed to delete theatre", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return theatreList.size
    }

    class TheatreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views from item_theatre.xml
        val theatreNameTextView: TextView = itemView.findViewById(R.id.theatreNameTextView)
        val theatreTypeTextView: TextView = itemView.findViewById(R.id.theatreTypeTextView)
        val theatreImageView: ImageView = itemView.findViewById(R.id.theatreImageView)
        val editTheatreButton: Button = itemView.findViewById(R.id.editTheatreButton)
        val deleteTheatreButton: Button = itemView.findViewById(R.id.deleteTheatreButton)
    }
}