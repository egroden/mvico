package com.egroden.teaco.sample.base

import android.app.Application
import com.egroden.teaco.sample.di.AppDi

class App : Application() {
    private val di: AppDi by lazy {
        AppDi(applicationContext)
    }

    fun inject(activity: AppActivity) =
        di.activityInjector.inject(activity)
}