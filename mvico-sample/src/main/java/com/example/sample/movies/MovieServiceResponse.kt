package com.example.sample.movies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val results: List<Movie>
)

@Serializable
data class Movie(
    @SerialName("id") val id: Int = 0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("title") val title: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("overview") val overview: String,
    @SerialName("vote_average") val voteAverage: String,
    @SerialName("backdrop_path") val backdropPath: String,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("popularity") val rating: Double
)