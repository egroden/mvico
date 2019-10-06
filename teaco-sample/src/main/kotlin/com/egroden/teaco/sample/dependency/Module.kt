package com.egroden.teaco.sample.dependency

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.SavedStateHandle
import com.egroden.teaco.AndroidConnector
import com.egroden.teaco.sample.movies.*

interface Module

class MoviesFragmentModule(movieRepository: MovieRepository) : Module {
    val factory: (SavedStateHandle) -> AndroidConnector<Action, SideEffect, State, Subscription> = {
        AndroidConnector(
            initialState = State(false, null, null),
            update = movieUpdater,
            effectHandler = MovieEffectHandler(movieRepository),
            savedStateHandle = it
        )
    }
}

class AppFragmentFactory(
    private val moviesFragmentModule: MoviesFragmentModule
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MovieFragment::class.java.name -> MovieFragment(moviesFragmentModule.factory)
            else -> super.instantiate(classLoader, className)
        }
    }
}