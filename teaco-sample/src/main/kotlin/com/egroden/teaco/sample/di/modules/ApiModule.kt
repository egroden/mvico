package com.egroden.teaco.sample.di.modules

import com.egroden.teaco.sample.BuildConfig
import com.egroden.teaco.sample.data.network.okhttp.AuthInterceptor
import com.egroden.teaco.sample.data.network.okhttp.OkHttpNetworkProvider

class ApiModule {
    private val interceptor: AuthInterceptor by lazy {
        AuthInterceptor(BuildConfig.API_KEY)
    }

    val networkProvider: OkHttpNetworkProvider by lazy {
        OkHttpNetworkProvider(interceptor)
    }
}