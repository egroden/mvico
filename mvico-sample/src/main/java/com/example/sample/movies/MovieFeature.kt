package com.example.sample.movies

import com.example.mvico.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class Subscription

sealed class Action {
    class LoadAction(val page: Int) : Action()
    class ShowResult(val result: List<Movie>) : Action()
    class ShowError(val error: Throwable) : Action()
}

data class State(
    val loading: Boolean = false,
    val data: List<Movie>? = null,
    val error: Throwable? = null
)

class MovieReducer(private val domain: Domain) : Reducer<State, Action, CommonEffectHandler.Effect<Action>> {
    override fun invoke(state: State, action: Action) =
        when (action) {
            is Action.LoadAction ->
                state.copy(loading = true, data = null, error = null) to
                        setOf(CommonEffectHandler.Effect(domain.makeWebEffect(action.page)))
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

class Domain(private val movieClient: OkHttpClient, private val baseUrl: HttpUrl) {
    fun makeWebEffect(page: Int): Eff<Action> {
        val request = Request.Builder()
            .url(
                baseUrl.newBuilder()
                    .addQueryParameter("sort_by", "popularity.desc")
                    .addQueryParameter("page", page.toString())
                    .build()
            )
            .build()
        val webEffect = SideEffects.WebEffect(movieClient, request)
        return handleResponse(webEffect.invoke(), Response.serializer()).map(::handleResult)
    }

    private fun <T> handleResponse(response: String, deserializationStrategy: DeserializationStrategy<T>) =
        object : Eff<T>{
            override fun invoke() =
                Json.nonstrict.parse(deserializationStrategy, response)
        }

    private fun handleResult(result: Either<Exception, Response>): Action = when (result) {
        is Either.Left -> Action.ShowError(result.value)
        is Either.Right -> Action.ShowResult(result.value.results)
    }
}