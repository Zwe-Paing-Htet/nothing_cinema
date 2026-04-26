package com.example.nothingcinema

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CinemaAdapterForCollection(
    private val cinemaList: List<DatabaseHelper.Cinema>,
    private val onCinemaClick: (DatabaseHelper.Cinema) -> Unit
) : RecyclerView.Adapter<CinemaAdapterForCollection.CinemaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cinema_collection, parent, false)
        return CinemaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CinemaViewHolder, position: Int) {
        val cinema = cinemaList[position]

        holder.cinemaNameTextView.text = cinema.name
        holder.cinemaTownshipTextView.text = cinema.township

        cinema.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.cinemaPosterImageView.setImageBitmap(bitmap)
        } ?: run {
            holder.cinemaPosterImageView.setImageResource(R.drawable.ic_placeholder)
        }

        holder.itemView.setOnClickListener {
            onCinemaClick(cinema)
        }
    }

    override fun getItemCount(): Int = cinemaList.size

    class CinemaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cinemaPosterImageView: ImageView =
            itemView.findViewById(R.id.cinemaPosterImageView)
        val cinemaNameTextView: TextView =
            itemView.findViewById(R.id.cinemaNameTextView)
        val cinemaTownshipTextView: TextView =
            itemView.findViewById(R.id.cinemaTownshipTextView)
    }
}