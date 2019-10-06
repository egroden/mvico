package com.egroden.teaco.sample

import android.app.Application
import com.egroden.teaco.sample.dependency.Injector
import com.egroden.teaco.sample.dependency.MoviesFragmentModule
import com.egroden.teaco.sample.dependency.NetworkProvider
import com.egroden.teaco.sample.movies.MovieRepository
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