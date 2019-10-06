package com.egroden.teaco.sample.data.network.okhttp

import okhttp3.Interceptor

class AuthInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain) =
        chain.proceed(
            chain.request()
                .newBuilder()
                .url(
                    chain.request().url
                        .newBuilder()
                        .addQueryParameter("api_key", apiKey)
                        .build()
                )
                .build()
        )
}
