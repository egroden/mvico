package com.egroden.teaco.sample.di

import android.content.Context
import com.egroden.teaco.sample.di.modules.ApiModule
import com.egroden.teaco.sample.di.modules.MoviesModule

class AppDi(private val context: Context) {
    private val apiModule = ApiModule()
    private val moviesFragmentModule = MoviesModule(apiModule)

    val activityInjector = ActivityInjector(moviesFragmentModule)
}