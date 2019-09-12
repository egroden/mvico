package com.example.sample.dependency

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.sample.core.Connector
import com.example.sample.core.MviFeature
import com.example.sample.movies.*

interface Module

class MoviesFragmentModule(movieRepository: MovieRepository): Module {
    private val feature = MviFeature<Action, SideEffect, State, Subscription>(
        initialState = State(false, null, null),
        reduce = movieReducer,
        effectHandler = MovieEffectHandler(movieRepository)
    )
    val connector = Connector(feature)
}

class AppFragmentFactory(private val moviesFragmentModule: MoviesFragmentModule): FragmentFactory(){
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            MovieFragment::class.java.name -> MovieFragment(moviesFragmentModule.connector)
            else -> super.instantiate(classLoader, className)
        }
    }
}