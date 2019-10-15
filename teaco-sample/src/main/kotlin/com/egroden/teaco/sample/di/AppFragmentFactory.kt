package com.egroden.teaco.sample.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.egroden.teaco.sample.di.modules.MoviesModule
import com.egroden.teaco.sample.presentation.movie.MovieFragment

class AppFragmentFactory(
    private val moviesModule: MoviesModule
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MovieFragment::class.java.name -> MovieFragment(feature = moviesModule.teaFeature)
            else -> super.instantiate(classLoader, className)
        }
    }
}