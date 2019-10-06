package com.example.sample

import android.app.Application
import com.example.sample.dependency.ActivityInjector
import com.example.sample.dependency.MoviesModule
import com.example.sample.dependency.NetworkModule
import com.example.sample.movies.Domain
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(
            ActivityInjector(
                MoviesModule(
                    Domain(
                        NetworkModule().client,
                        BuildConfig.API_URL.toHttpUrlOrNull()!!
                    )
                )
            )
        )
    }
}