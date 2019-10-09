package com.egroden.teaco.sample.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.egroden.teaco.sample.R
import com.egroden.teaco.sample.di.AppFragmentFactory
import com.egroden.teaco.sample.presentation.movie.MovieFragment

class AppActivity : AppCompatActivity() {
    lateinit var fragmentFactory: AppFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.fragmentFactory = fragmentFactory

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack("movies")
                .add(
                    R.id.activity_main,
                    MovieFragment::class.java,
                    null
                )
                .commit()
        }
    }
}
