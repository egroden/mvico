package com.egroden.sample.movies

import com.egroden.sample.either
import com.egroden.sample.map
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class MovieRepository(private val movieClient: OkHttpClient, private val baseUrl: HttpUrl) {
    @UseExperimental(UnstableDefault::class)
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