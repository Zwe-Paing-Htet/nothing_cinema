package com.example.nothingcinema

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CinemaAdapterForView(
    private val cinemaList: MutableList<DatabaseHelper.Cinema>,
    private val onCreateTheatreClick: (DatabaseHelper.Cinema) -> Unit // Lambda to handle Create Theatre button click
) : RecyclerView.Adapter<CinemaAdapterForView.CinemaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaViewHolder {
        // Inflate the custom item layout for View Cinema (item_cinema.xml)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cinema, parent, false)
        return CinemaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CinemaViewHolder, position: Int) {
        val cinema = cinemaList[position]

        // Bind data to the views
//        holder.cinemaNameTextView.text = cinema.name
//        holder.cinemaTownshipTextView.text = "Township: ${cinema.township}"
//        holder.cinemaFullAddressTextView.text = "Address: ${cinema.fullAddress}"

        holder.cinemaNameTextView.text = cinema.name
        holder.cinemaTownshipTextView.text = cinema.township
        holder.cinemaFullAddressTextView.text = cinema.fullAddress

        // Resize the cinema image before setting it
        cinema.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            val resizedBitmap = resizeImage(bitmap, 300, 300)  // Resize the image
            holder.cinemaPosterImageView.setImageBitmap(resizedBitmap)
        }

        // Set click listener for the Edit button
        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditCinemaActivity::class.java).apply {
                putExtra("cinema_id", cinema.id)
                putExtra("cinema_name", cinema.name)
                putExtra("cinema_township", cinema.township)
                putExtra("cinema_full_address", cinema.fullAddress)
                putExtra("cinema_google_map_url", cinema.googleMapUrl) // Pass the Google Map URL
                putExtra("cinema_image", cinema.image) // Pass the cinema poster image (byte array)
            }
            context.startActivity(intent)
        }

        // Set click listener for the Create Theatre button
        holder.createTheatreButton.setOnClickListener {
            onCreateTheatreClick(cinema)  // Invoke the lambda to handle the click
        }

        // Set click listener for the Delete button
        holder.deleteButton.setOnClickListener {
            val dbHelper = DatabaseHelper(holder.itemView.context)
            val deletedRows = dbHelper.deleteCinema(cinema.id)

            // If deletion was successful, remove the cinema from the list and notify the adapter
            if (deletedRows > 0) {
                cinemaList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(holder.itemView.context, "Cinema deleted successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Failed to delete cinema", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return cinemaList.size
    }

    class CinemaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views from item_cinema.xml
        val cinemaPosterImageView: ImageView =
            itemView.findViewById(R.id.cinemaPosterImageView)  // ImageView for cinema poster
        val cinemaNameTextView: TextView = itemView.findViewById(R.id.cinemaNameTextView)
        val cinemaTownshipTextView: TextView = itemView.findViewById(R.id.cinemaTownshipTextView)
        val cinemaFullAddressTextView: TextView =
            itemView.findViewById(R.id.cinemaFullAddressTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val createTheatreButton: Button = itemView.findViewById(R.id.createTheatreButton)
    }

    // Resize the image
    private fun resizeImage(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        // Calculate the aspect ratio
        val aspectRatio: Float = image.width.toFloat() / image.height.toFloat()

        var newWidth = maxWidth
        var newHeight = maxHeight

        // Adjust the width and height according to the aspect ratio
        if (aspectRatio > 1) { // Landscape
            newHeight = (maxWidth / aspectRatio).toInt()
        } else { // Portrait
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        // Resize the bitmap
        return Bitmap.createScaledBitmap(image, newWidth, newHeight, false)
    }
}