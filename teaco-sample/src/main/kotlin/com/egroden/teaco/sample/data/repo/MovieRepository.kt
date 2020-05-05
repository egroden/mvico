package com.egroden.teaco.sample.data.repo

import com.egroden.teaco.Either
import com.egroden.teaco.either
import com.egroden.teaco.sample.data.dto.MovieDto
import com.egroden.teaco.sample.data.network.NetworkClient

class MovieRepository(private val client: NetworkClient) {
    suspend fun loadMovies(page: Int): Either<Exception, List<MovieDto>> =
        either { client.loadMovies(page) }
}