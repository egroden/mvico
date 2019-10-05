package com.egroden.sample.movies

import com.egroden.mvico.EffectHandler
import com.egroden.mvico.Reducer
import com.egroden.sample.Either
import kotlinx.coroutines.flow.flow

class Subscription

sealed class Action {
    class LoadAction(val page: Int) : Action()
    class ShowResult(val result: List<Movie>) : Action()
    class ShowError(val error: Throwable) : Action()
}

sealed class SideEffect {
    class LoadMovies(val page: Int) : SideEffect()
}

data class State(
    val loading: Boolean = false,
    val data: List<Movie>? = null,
    val error: Throwable? = null
)

val movieReducer = object : Reducer<State, Action, SideEffect> {
    override fun invoke(state: State, action: Action) =
        when (action) {
            is Action.LoadAction -> state.copy(loading = true, data = null, error = null) to setOf(
                SideEffect.LoadMovies(action.page)
            )
            is Action.ShowResult -> state.copy(
                loading = false,
                data = action.result,
                error = null
            ) to emptySet()
            is Action.ShowError -> state.copy(
                loading = false,
                data = null,
                error = action.error
            ) to emptySet()
        }
}

class MovieEffectHandler(
    private val movieRepository: MovieRepository
) : EffectHandler<SideEffect, Action> {
    override fun handle(sideEffect: SideEffect) = when (sideEffect) {
        is SideEffect.LoadMovies -> flow {
            when (val result = movieRepository.loadMovies(sideEffect.page)) {
                is Either.Left -> emit(Action.ShowError(result.value))
                is Either.Right -> emit(Action.ShowResult(result.value))
            }
        }
    }
}

