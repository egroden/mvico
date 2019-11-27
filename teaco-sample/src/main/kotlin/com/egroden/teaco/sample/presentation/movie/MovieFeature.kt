package com.egroden.teaco.sample.presentation.movie

import android.os.Parcelable
import com.egroden.teaco.EffectHandler
import com.egroden.teaco.Either
import com.egroden.teaco.UpdateResponse
import com.egroden.teaco.Updater
import com.egroden.teaco.sample.data.repo.MovieRepository
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class Subscription(
    val error: Throwable? = null
)

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
    val data: List<MovieModel>? = null
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

val movieUpdater = object : Updater<State, Action, Subscription, SideEffect> {
    override fun invoke(
        state: State,
        action: Action
    ): UpdateResponse<State, Subscription, SideEffect> =
        when (action) {
            is Action.LoadAction ->
                UpdateResponse(
                    state = state.copy(loading = true, data = null),
                    sideEffects = setOf(SideEffect.LoadMovies(action.page))
                )
            is Action.ShowResult ->
                UpdateResponse(
                    state = state.copy(loading = false, data = action.result)
                )
            is Action.ShowError ->
                UpdateResponse(
                    state = state.copy(loading = false, data = null),
                    subscription = Subscription(action.error)
                )
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
                        Action.ShowError(result.value)
                    )

                    is Either.Right -> emit(
                        Action.ShowResult(result.value.map(MovieMapper::toModel))
                    )
                }
        }
    }
}

