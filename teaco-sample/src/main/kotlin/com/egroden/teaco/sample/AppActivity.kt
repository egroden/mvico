package com.egroden.teaco.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.egroden.teaco.sample.movies.MovieFragment

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
            println("start transaction")
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
