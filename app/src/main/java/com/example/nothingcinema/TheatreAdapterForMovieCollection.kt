package com.example.nothingcinema

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TheatreAdapterForMovieCollection(
    theatreList: List<DatabaseHelper.Theatre>,
    private val onBookClick: (DatabaseHelper.Theatre) -> Unit,
    private val onBuyClick: (DatabaseHelper.Theatre) -> Unit
) : RecyclerView.Adapter<TheatreAdapterForMovieCollection.TheatreViewHolder>() {

    private val uniqueTheatres: List<DatabaseHelper.Theatre> = removeDuplicates(theatreList)

    private fun removeDuplicates(theatreList: List<DatabaseHelper.Theatre>): List<DatabaseHelper.Theatre> {
        val uniqueTheatreMap = mutableMapOf<Long, DatabaseHelper.Theatre>()
        for (theatre in theatreList) {
            if (!uniqueTheatreMap.containsKey(theatre.id)) {
                uniqueTheatreMap[theatre.id] = theatre
            }
        }
        return uniqueTheatreMap.values.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheatreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_theatre_movie_collection, parent, false)
        return TheatreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TheatreViewHolder, position: Int) {
        val theatre = uniqueTheatres[position]

        holder.theatreNameTextView.text = theatre.name
        holder.theatreTypeTextView.text = theatre.type ?: "Standard"

        theatre.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.theatreImageView.setImageBitmap(bitmap)
        } ?: holder.theatreImageView.setImageResource(R.drawable.ic_placeholder)

        holder.bookButton.setOnClickListener {
            onBookClick(theatre)
        }

        holder.buyButton.setOnClickListener {
            onBuyClick(theatre)
        }
    }

    override fun getItemCount(): Int {
        return uniqueTheatres.size
    }

    class TheatreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val theatreNameTextView: TextView = itemView.findViewById(R.id.theatreNameTextView)
        val theatreTypeTextView: TextView = itemView.findViewById(R.id.theatreTypeTextView)
        val theatreImageView: ImageView = itemView.findViewById(R.id.theatreImageView)
        val bookButton: Button = itemView.findViewById(R.id.bookButton)
        val buyButton: Button = itemView.findViewById(R.id.buyButton)
    }
}