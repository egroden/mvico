package com.example.sample.movies

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.example.sample.*

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = arrayListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) = MovieViewHolder(
        viewGroup.imageView {
            adjustViewBounds = true
            size(Size.MATCH_PARENT, Size.WRAP_CONTENT)
        }
    )

    override fun onBindViewHolder(movieViewHolder: MovieViewHolder, i: Int) = movieViewHolder.bind(movies[i])

    override fun getItemCount() = movies.size

    fun update(newMovies: List<Movie>) {
        val moviesDiffResult = DiffUtil.calculateDiff(MovieDiffUtilCallback(movies, newMovies))
        movies = newMovies
        moviesDiffResult.dispatchUpdatesTo(this)
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: Movie) = (itemView as ImageView).loadMoviePreview(model.backdropPath)
    }
}