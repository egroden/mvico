package com.egroden.teaco.sample.dependency

import com.egroden.teaco.sample.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class NetworkProvider {
    val client = OkHttpClient().newBuilder()
        .addInterceptor(AuthInterceptor(BuildConfig.API_KEY))
        .build()
}

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
