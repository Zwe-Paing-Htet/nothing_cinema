package com.example.nothingcinema

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class AdminSeatedTheatreAdapter(
    private val theatreList: List<DatabaseHelper.Theatre>
) : RecyclerView.Adapter<AdminSeatedTheatreAdapter.SeatedTheatreViewHolder>() {

    class SeatedTheatreViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatedTheatreViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seated_theatre_button, parent, false) as Button
        return SeatedTheatreViewHolder(button)
    }

    override fun onBindViewHolder(holder: SeatedTheatreViewHolder, position: Int) {
        val theatre = theatreList[position]
        holder.button.text = theatre.name

        holder.button.setOnClickListener {
            val context = holder.button.context
            val intent = Intent(context, AdminTheatreShowtimesActivity::class.java).apply {
                putExtra("theatre_id", theatre.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = theatreList.size
}