package com.example.nothingcinema

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapterForCollection(
    private val movieList: List<DatabaseHelper.Movie>,
    private val onMovieClick: (DatabaseHelper.Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapterForCollection.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_collection, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]

        holder.movieNameTextView.text = movie.name
        holder.movieRuntimeTextView.text = "${movie.runtime} min"
        holder.movieGenreTextView.text = movie.genre ?: "Unknown genre"

        movie.posterImage?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.moviePosterImageView.setImageBitmap(bitmap)
        } ?: holder.moviePosterImageView.setImageResource(R.drawable.ic_placeholder)

        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moviePosterImageView: ImageView = itemView.findViewById(R.id.moviePosterImageView)
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
        val movieRuntimeTextView: TextView = itemView.findViewById(R.id.movieRuntimeTextView)
        val movieGenreTextView: TextView = itemView.findViewById(R.id.movieGenreTextView)
    }
}