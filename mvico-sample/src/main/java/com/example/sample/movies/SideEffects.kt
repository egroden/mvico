package com.example.sample.movies

import com.example.mvico.Eff
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

sealed class SideEffects {
    class WebEffect<T>(
        private val movieClient: OkHttpClient,
        private val request: Request,
        private val serializer: DeserializationStrategy<T>
    ) : SideEffects(), Eff<T> {
        override fun invoke(): T =
            Json.nonstrict.parse(serializer, movieClient.newCall(request).execute().body!!.string())
    }
}