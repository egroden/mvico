package com.egroden.teaco.sample.data.network

import com.egroden.teaco.sample.data.dto.MovieDto
import com.egroden.teaco.sample.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class NetworkClient(
    private val client: OkHttpClient,
    private val baseUrl: HttpUrl,
    private val json: Json
) {
    suspend fun loadMovies(page: Int): List<MovieDto> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(
                baseUrl.newBuilder()
                    .addQueryParameter("sort_by", "popularity.desc")
                    .addQueryParameter("page", page.toString())
                    .build()
            )
            .build()

        val response = client
            .newCall(request)
            .execute()
            .body!!
            .string()

        return@withContext json.parse(Response.serializer(), response).results
    }
}