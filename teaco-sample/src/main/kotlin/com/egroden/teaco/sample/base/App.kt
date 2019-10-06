package com.egroden.teaco.sample.base

import android.app.Application
import com.egroden.teaco.sample.di.AppDi

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val di = AppDi()
        registerActivityLifecycleCallbacks(di.activityInjector)
    }
}