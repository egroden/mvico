package com.egroden.sample.dependency

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.egroden.sample.AppActivity

class Injector(
    private val moviesFragmentModule: MoviesFragmentModule
) : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        when (activity) {
            is AppActivity -> (activity as? FragmentActivity)
                ?.supportFragmentManager
                ?.fragmentFactory = AppFragmentFactory(moviesFragmentModule)
        }
    }

    override fun onActivityPaused(activity: Activity?) = Unit

    override fun onActivityResumed(activity: Activity?) = Unit

    override fun onActivityStarted(activity: Activity?) = Unit

    override fun onActivityDestroyed(activity: Activity?) = Unit

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity?) = Unit
}