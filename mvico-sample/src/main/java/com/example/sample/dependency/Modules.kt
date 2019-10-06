package com.example.sample.dependency

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.mvico.CommonEffectHandler
import com.example.mvico.Connector
import com.example.mvico.MviFeature
import com.example.mvico_android.AndroidConnector
import com.example.sample.BuildConfig
import com.example.sample.movies.*
import kotlinx.coroutines.MainScope
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class MoviesModule(domain: Domain){
    private val feature = MviFeature<Action, CommonEffectHandler.Effect<Action>, State, Subscription>(
        initialState = State(false, null, null),
        reduce = MovieReducer(domain),
        effectHandler = CommonEffectHandler()
    )
    val connector = Connector(MainScope(), feature)
    val androidConnector = AndroidConnector(connector)
}

class AppFragmentFactory(private val moviesModule: MoviesModule): FragmentFactory(){
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            MovieFragment::class.java.name -> MovieFragment(moviesModule.androidConnector)
            else -> super.instantiate(classLoader, className)
        }
    }
}

class NetworkModule {
    val client = OkHttpClient().newBuilder()
        .addInterceptor(AuthInterceptor(BuildConfig.API_KEY))
        .build()
}

class AuthInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain) =
        chain.proceed(
            chain.request()
                .newBuilder()
                .url(
                    chain.request().url
                        .newBuilder()
                        .addQueryParameter("api_key", apiKey)
                        .build()
                )
                .build()
        )
}