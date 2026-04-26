package com.example.nothingcinema

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ShowtimeAdapterForView(
    private val showtimeList: List<DatabaseHelper.Showtime>,
    private val dbHelper: DatabaseHelper,  // Pass the DatabaseHelper instance here
    private val onShowtimeClick: (DatabaseHelper.Showtime) -> Unit // Lambda for showtime detail page navigation
) : RecyclerView.Adapter<ShowtimeAdapterForView.ShowtimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_showtime, parent, false)
        return ShowtimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        val showtime = showtimeList[position]

        // Fetch movie and theatre names from their ids using dbHelper
        val movie = dbHelper.getMovieById(showtime.movieId)
        val theatre = dbHelper.getTheatreById(showtime.theatreId)

        // Set data to the views
//        holder.showtimeDateTextView.text = "Showtime: ${showtime.showtimeDate}"
//        holder.showtimeTimeTextView.text = "Time: ${showtime.showtimeTime}"
//        holder.movieNameTextView.text = "Movie: ${movie?.name ?: "Unknown Movie"}"
//        holder.theatreNameTextView.text = "Theatre: ${theatre?.name ?: "Unknown Theatre"}"

        holder.showtimeDateTextView.text = showtime.showtimeDate
        holder.showtimeTimeTextView.text = showtime.showtimeTime
        holder.movieNameTextView.text = movie?.name ?: "Unknown Movie"
        holder.theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"

        // Set click listener for the showtime item (Showtime Details Page)
        holder.itemView.setOnClickListener {
            onShowtimeClick(showtime)
        }

        // Set click listener for the "Delete Showtime" button
        holder.deleteShowtimeButton.setOnClickListener {
            val context = holder.itemView.context
            val dbHelper = DatabaseHelper(context)

            // Delete the showtime from the database
            val deletedRows = dbHelper.deleteShowtime(showtime.id)

            // If deletion was successful, remove the showtime from the list and notify the adapter
            if (deletedRows > 0) {
                Toast.makeText(context, "Showtime deleted successfully!", Toast.LENGTH_SHORT).show()
                (showtimeList as MutableList).removeAt(position)  // Remove the item from the list
                notifyItemRemoved(position)  // Notify the adapter to remove the item
            } else {
                Toast.makeText(context, "Failed to delete showtime", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for the "Edit Showtime" button
        holder.editShowtimeButton.setOnClickListener {
            val context = holder.itemView.context

            // Pass the showtime ID to the EditShowtimeActivity
            val intent = Intent(context, EditShowtimeActivity::class.java).apply {
                putExtra("showtime_id", showtime.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return showtimeList.size
    }

    class ShowtimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showtimeDateTextView: TextView = itemView.findViewById(R.id.showtimeDateTextView)
        val showtimeTimeTextView: TextView = itemView.findViewById(R.id.showtimeTimeTextView)
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
        val theatreNameTextView: TextView = itemView.findViewById(R.id.theatreNameTextView)
        val deleteShowtimeButton: Button = itemView.findViewById(R.id.deleteShowtimeButton)
        val editShowtimeButton: Button = itemView.findViewById(R.id.editShowtimeButton)  // Edit button
    }
}