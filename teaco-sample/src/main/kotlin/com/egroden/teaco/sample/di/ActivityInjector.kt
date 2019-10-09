package com.egroden.teaco.sample.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.egroden.teaco.sample.base.AppActivity
import com.egroden.teaco.sample.di.modules.MoviesModule

class ActivityInjector(
    private val moviesModule: MoviesModule
) : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        (activity as? AppActivity)?.fragmentFactory = AppFragmentFactory(moviesModule)
    }

    override fun onActivityPaused(activity: Activity?) = Unit

    override fun onActivityResumed(activity: Activity?) = Unit

    override fun onActivityStarted(activity: Activity?) = Unit

    override fun onActivityDestroyed(activity: Activity?) = Unit

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity?) = Unit
}