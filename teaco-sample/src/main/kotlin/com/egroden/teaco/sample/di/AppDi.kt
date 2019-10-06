package com.egroden.teaco.sample.di

import com.egroden.teaco.sample.di.modules.ApiModule
import com.egroden.teaco.sample.di.modules.MoviesModule

class AppDi {
    private val apiModule = ApiModule()
    private val moviesFragmentModule = MoviesModule(apiModule)

    val activityInjector = ActivityInjector(moviesFragmentModule)
}