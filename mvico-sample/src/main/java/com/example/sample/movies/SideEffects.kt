package com.example.sample.movies

import com.example.mvico.Eff
import okhttp3.OkHttpClient
import okhttp3.Request

sealed class SideEffects {
    class WebEffect(
        private val movieClient: OkHttpClient,
        private val request: Request
    ) : SideEffects(), Eff<String> {
        override fun invoke() =
            movieClient.newCall(request).execute().body!!.string()
    }
}