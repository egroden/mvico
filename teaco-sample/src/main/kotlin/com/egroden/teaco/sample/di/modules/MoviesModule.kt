package com.egroden.teaco.sample.di.modules

import com.egroden.teaco.Feature
import com.egroden.teaco.TeaFeature
import com.egroden.teaco.sample.BuildConfig
import com.egroden.teaco.sample.data.network.NetworkClient
import com.egroden.teaco.sample.data.repo.MovieRepository
import com.egroden.teaco.sample.presentation.movie.*
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class MoviesModule(private val apiModule: ApiModule) {
    @OptIn(UnstableDefault::class)
    private val json: Json by lazy { Json.nonstrict }

    private val networkClient: NetworkClient by lazy {
        NetworkClient(
            client = apiModule.networkProvider.client,
            baseUrl = BuildConfig.API_URL.toHttpUrlOrNull()!!,
            json = json
        )
    }

    private val moviesRepository: MovieRepository by lazy {
        MovieRepository(networkClient)
    }

    fun createTeaFeature(oldState: State?): Feature<Action, SideEffect, State, Subscription> =
        TeaFeature(
            initialState = oldState ?: State(false, null),
            updater = movieUpdater,
            effectHandler = MovieEffectHandler(moviesRepository)
        )
}