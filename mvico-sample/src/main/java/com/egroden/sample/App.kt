package com.egroden.sample

import android.app.Application
import com.egroden.sample.dependency.Injector
import com.egroden.sample.dependency.MoviesFragmentModule
import com.egroden.sample.dependency.NetworkProvider
import com.egroden.sample.movies.MovieRepository
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