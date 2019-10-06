package com.egroden.teaco.sample.data.network.okhttp

import okhttp3.Interceptor
import okhttp3.OkHttpClient

class OkHttpNetworkProvider(interceptor: Interceptor) {
    val client = OkHttpClient()
        .newBuilder()
        .addInterceptor(interceptor)
        .build()
}