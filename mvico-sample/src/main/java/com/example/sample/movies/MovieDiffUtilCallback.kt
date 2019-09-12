package com.example.sample.movies

import androidx.recyclerview.widget.DiffUtil

class MovieDiffUtilCallback(private val oldList: List<Movie>, private val newList: List<Movie>): DiffUtil.Callback() {
    override fun areItemsTheSame(p0: Int, p1: Int) = oldList[p0].id == newList[p1].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(p0: Int, p1: Int) = oldList[p0] == newList[p1]
}