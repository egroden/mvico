package com.egroden.teaco.sample.presentation.movie

import android.os.Parcelable
import com.egroden.teaco.EffectHandler
import com.egroden.teaco.Updater
import com.egroden.teaco.sample.Either
import com.egroden.teaco.sample.data.repo.MovieRepository
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Subscription

sealed class Action {
    class LoadAction(val page: Int) : Action()
    class ShowResult(val result: List<MovieModel>) : Action()
    class ShowError(val error: Throwable) : Action()
}

sealed class SideEffect {
    class LoadMovies(val page: Int) : SideEffect()
}

@Parcelize
data class State(
    val loading: Boolean = false,
    val data: List<MovieModel>? = null,
    val error: Throwable? = null
) : Parcelable

@Parcelize
class MovieModel(
    val id: Int,
    val voteCount: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val voteAverage: String,
    val backdropPath: String,
    val releaseDate: String,
    val rating: Double
) : Parcelable

val movieUpdater: Updater<State, Action, SideEffect> = { state, action ->
    when (action) {
        is Action.LoadAction ->
            state.copy(loading = true, data = null, error = null) to setOf(
                SideEffect.LoadMovies(action.page)
            )
        is Action.ShowResult ->
            state.copy(loading = false, data = action.result, error = null) to emptySet()
        is Action.ShowError ->
            state.copy(loading = false, data = null, error = action.error) to emptySet()
    }
}

class MovieEffectHandler(
    private val movieRepository: MovieRepository
) : EffectHandler<SideEffect, Action> {
    override fun invoke(sideEffect: SideEffect): Flow<Action> = flow {
        when (sideEffect) {
            is SideEffect.LoadMovies ->
                when (val result = movieRepository.loadMovies(sideEffect.page)) {
                    is Either.Left -> emit(
                        Action.ShowError(
                            result.value
                        )
                    )

                    is Either.Right -> emit(
                        Action.ShowResult(
                            result.value.map(MovieMapper::toModel)
                        )
                    )
                }
        }
    }
}

