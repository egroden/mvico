package com.example.sample

import android.app.Application
import com.example.sample.dependency.Injector
import com.example.sample.dependency.MoviesFragmentModule
import com.example.sample.dependency.NetworkProvider
import com.example.sample.movies.MovieRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(
            Injector(
                MoviesFragmentModule(
                    MovieRepository(
                        NetworkProvider().client,
                        BuildConfig.API_URL.toHttpUrlOrNull()!!
                    )
                )
            )
        )
    }
}