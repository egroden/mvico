package com.example.sample.movies

import com.example.sample.either
import com.example.sample.map
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class MovieRepository(private val movieClient: OkHttpClient, private val baseUrl: HttpUrl) {

    fun loadMovies(page: Int) =
        either {
            Json.nonstrict.parse(
                Response.serializer(),
                movieClient.newCall(
                    Request.Builder()
                        .url(
                            baseUrl.newBuilder()
                                .addQueryParameter("sort_by", "popularity.desc")
                                .addQueryParameter("page", page.toString())
                                .build()
                        )
                        .build()
                ).execute().body!!.string()
            )
        }.map { it.results }
}