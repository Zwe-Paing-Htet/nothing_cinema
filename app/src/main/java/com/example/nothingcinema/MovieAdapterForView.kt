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

class MovieAdapterForView(
    private val movieList: MutableList<DatabaseHelper.Movie>,
    private val onCreateMovieClick: (DatabaseHelper.Movie) -> Unit // Lambda for movie selection
) : RecyclerView.Adapter<MovieAdapterForView.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Inflate the custom item layout for View Movie (item_movie.xml)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]

        // Bind data to the views
//        holder.movieNameTextView.text = movie.name
//        holder.movieRuntimeTextView.text = "Runtime: ${movie.runtime} minutes"
//        holder.movieGenreTextView.text = "Genre: ${movie.genre}"
        holder.movieNameTextView.text = movie.name
        holder.movieRuntimeTextView.text = "${movie.runtime} min"
        holder.movieGenreTextView.text = movie.genre ?: "Unknown genre"


        // Resize and set the movie poster (image)
        movie.posterImage?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            val resizedBitmap = resizeImage(bitmap, 300, 300)  // Resize the image
            holder.moviePosterImageView.setImageBitmap(resizedBitmap)
        }

        // Set click listener for the Edit button
        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditMovieActivity::class.java).apply {
                putExtra("movie_id", movie.id)
                putExtra("movie_name", movie.name)
                putExtra("movie_runtime", movie.runtime)
                putExtra("movie_genre", movie.genre)
                putExtra("movie_trailer", movie.trailerUrl)
                putExtra("movie_status", movie.status)
                putExtra("movie_poster", movie.posterImage) // Pass the movie poster (byte array)
            }
            context.startActivity(intent)
        }

        // Set click listener for the Create Theatre button
        holder.addShowtimeButton.setOnClickListener {
            onCreateMovieClick(movie)  // Invoke the lambda to handle the click
        }


        // Set click listener for the Delete button
        holder.deleteButton.setOnClickListener {
            val dbHelper = DatabaseHelper(holder.itemView.context)
            val deletedRows = dbHelper.deleteMovie(movie.id)

            // If deletion was successful, remove the movie from the list and notify the adapter
            if (deletedRows > 0) {
                movieList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(holder.itemView.context, "Movie deleted successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Failed to delete movie", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views from item_movie.xml
        val moviePosterImageView: ImageView = itemView.findViewById(R.id.moviePosterImageView)
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
        val movieRuntimeTextView: TextView = itemView.findViewById(R.id.movieRuntimeTextView)
        val movieGenreTextView: TextView = itemView.findViewById(R.id.movieGenreTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val addShowtimeButton: Button = itemView.findViewById(R.id.addShowtimeButton)
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