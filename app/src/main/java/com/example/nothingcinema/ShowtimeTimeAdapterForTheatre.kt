package com.example.nothingcinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowtimeTimeAdapterForTheatre(
    private val showtimeList: List<DatabaseHelper.Showtime>,
    private val onShowtimeTimeClick: (DatabaseHelper.Showtime) -> Unit
) : RecyclerView.Adapter<ShowtimeTimeAdapterForTheatre.ShowtimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_showtime_time_for_theatre, parent, false)
        return ShowtimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        val showtime = showtimeList[position]
        holder.showtimeTimeTextView.text = showtime.showtimeTime

        holder.itemView.setOnClickListener {
            onShowtimeTimeClick(showtime)
        }
    }

    override fun getItemCount(): Int {
        return showtimeList.size
    }

    class ShowtimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showtimeTimeTextView: TextView = itemView.findViewById(R.id.showtimeTimeTextView)
    }
}