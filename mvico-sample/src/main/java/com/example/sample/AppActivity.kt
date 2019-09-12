package com.example.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.sample.movies.MovieFragment

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            frameLayout {
                id(frameLayoutID)
                size(Size.MATCH_PARENT, Size.MATCH_PARENT)
            }
        )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack("movies")
                .add(
                    frameLayoutID,
                    supportFragmentManager.fragmentFactory.instantiate(
                        classLoader,
                        MovieFragment::class.java.name
                    ),
                    MovieFragment.TAG
                )
                .commit()
        }
    }

    companion object {
        private const val frameLayoutID = 1
    }
}
