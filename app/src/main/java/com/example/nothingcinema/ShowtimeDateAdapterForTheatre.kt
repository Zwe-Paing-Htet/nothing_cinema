package com.example.nothingcinema
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowtimeDateAdapterForTheatre(
    private val showtimeList: List<DatabaseHelper.Showtime>,
    private val onDateClick: (String) -> Unit
) : RecyclerView.Adapter<ShowtimeDateAdapterForTheatre.ShowtimeViewHolder>() {

    private val uniqueShowtimeDates: List<String>

    init {
        val dateSet = mutableSetOf<String>()
        for (showtime in showtimeList) {
            dateSet.add(showtime.showtimeDate)
        }
        uniqueShowtimeDates = dateSet.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_showtime_date_for_theatre, parent, false)
        return ShowtimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        val uniqueDate = uniqueShowtimeDates[position]
        holder.showtimeDateTextView.text = uniqueDate

        holder.itemView.setOnClickListener {
            onDateClick(uniqueDate)
        }
    }

    override fun getItemCount(): Int {
        return uniqueShowtimeDates.size
    }

    class ShowtimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showtimeDateTextView: TextView = itemView.findViewById(R.id.showtimeDateTextView)
    }
}