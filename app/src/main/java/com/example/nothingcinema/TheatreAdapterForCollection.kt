package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TheatreAdapterForCollection(
    private val theatreList: List<DatabaseHelper.Theatre>,
    private val onTheatreClick: (DatabaseHelper.Theatre) -> Unit // Lambda for theatre click handling
) : RecyclerView.Adapter<TheatreAdapterForCollection.TheatreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheatreViewHolder {
        // Inflate the custom item layout for Theatre (item_theatre.xml)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_theatre_collection, parent, false)
        return TheatreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TheatreViewHolder, position: Int) {
        val theatre = theatreList[position]

        // Bind data to the views
        holder.theatreNameTextView.text = theatre.name
//        holder.theatreTypeTextView.text = "Type: ${theatre.type}"
        holder.theatreTypeTextView.text = theatre.type ?: "Standard"

        // Set the theatre image (if available)
        theatre.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.theatreImageView.setImageBitmap(bitmap)
        }

        // Set click listener for the theatre item
        holder.itemView.setOnClickListener {
            onTheatreClick(theatre)  // Invoke the lambda to handle the click
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
    }
}