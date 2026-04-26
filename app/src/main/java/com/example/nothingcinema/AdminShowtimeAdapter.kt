package com.example.nothingcinema

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminShowtimeAdapter(
    private val showtimeList: List<DatabaseHelper.Showtime>,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<AdminShowtimeAdapter.ShowtimeViewHolder>() {

    class ShowtimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.showtimeItemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_showtime, parent, false)
        return ShowtimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        val showtime = showtimeList[position]
        val movie = dbHelper.getMovieById(showtime.movieId)

        holder.textView.text =
//            "${movie?.name ?: "Unknown Movie"}\n${showtime.showtimeDate} at ${showtime.showtimeTime}"
            "${movie?.name ?: "Unknown Movie"}\n${showtime.showtimeDate} • ${showtime.showtimeTime}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminSeatStatusActivity::class.java).apply {
                putExtra("showtime_id", showtime.id)
                putExtra("theatre_id", showtime.theatreId)
                putExtra("movie_id", showtime.movieId)
                putExtra("showtime_date", showtime.showtimeDate)
                putExtra("showtime_time", showtime.showtimeTime)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = showtimeList.size
}