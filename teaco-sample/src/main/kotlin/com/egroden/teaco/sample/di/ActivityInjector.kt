package com.egroden.teaco.sample.di

import com.egroden.teaco.sample.base.AppActivity
import com.egroden.teaco.sample.di.modules.MoviesModule

class ActivityInjector(
    private val moviesModule: MoviesModule
) {
    fun inject(activity: AppActivity) {
        activity.supportFragmentManager.fragmentFactory = AppFragmentFactory(moviesModule)
    }
}