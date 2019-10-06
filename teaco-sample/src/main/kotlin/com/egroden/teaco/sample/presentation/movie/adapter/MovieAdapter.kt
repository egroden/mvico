package com.egroden.teaco.sample.presentation.movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egroden.teaco.sample.R
import com.egroden.teaco.sample.presentation.loadMoviePreview
import com.egroden.teaco.sample.presentation.movie.MovieModel
import kotlinx.android.extensions.LayoutContainer

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private var movies: List<MovieModel> = arrayListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) =
        MovieViewHolder(
            LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.movie_image_view, viewGroup, false)
        )

    override fun onBindViewHolder(movieViewHolder: MovieViewHolder, i: Int) =
        movieViewHolder.bind(movies[i])

    override fun getItemCount() = movies.size

    fun update(newMovies: List<MovieModel>) {
        val moviesDiffResult = DiffUtil.calculateDiff(
            MovieDiffUtilCallback(
                movies,
                newMovies
            )
        )
        movies = newMovies
        moviesDiffResult.dispatchUpdatesTo(this)
    }

    class MovieViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bind(model: MovieModel) = (itemView as ImageView).loadMoviePreview(model.backdropPath)
    }
}