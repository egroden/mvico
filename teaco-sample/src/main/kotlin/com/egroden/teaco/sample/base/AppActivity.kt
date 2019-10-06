package com.egroden.teaco.sample.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.egroden.teaco.sample.R
import com.egroden.teaco.sample.presentation.movie.MovieFragment

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack("movies")
                .add(
                    R.id.activity_main,
                    supportFragmentManager.fragmentFactory.instantiate(
                        classLoader,
                        MovieFragment::class.java.name
                    ),
                    MovieFragment.TAG
                )
                .commit()
        }
    }
}
