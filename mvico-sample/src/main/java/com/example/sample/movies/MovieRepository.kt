package com.example.sample.movies

import com.example.sample.Either
import com.example.sample.either
import com.example.sample.map
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class MovieRepository(private val movieClient: OkHttpClient, private val baseUrl: HttpUrl) {

    fun loadMovies(request: Request): Either<Exception, List<Movie>> {
        return either {
            Json.nonstrict.parse(
                Response.serializer(),
                movieClient.newCall(
                    request
                ).execute().body!!.string()
            )
        }.map { it.results }
    }
}
