package com.egroden.teaco.sample.presentation.movie.adapter

import androidx.recyclerview.widget.DiffUtil
import com.egroden.teaco.sample.presentation.movie.MovieModel

class MovieDiffUtilCallback(
    private val oldList: List<MovieModel>,
    private val newList: List<MovieModel>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(p0: Int, p1: Int) = oldList[p0].id == newList[p1].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(p0: Int, p1: Int) = oldList[p0] == newList[p1]
}