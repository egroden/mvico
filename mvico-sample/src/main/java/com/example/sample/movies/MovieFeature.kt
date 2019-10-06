package com.example.sample.movies

import com.example.sample.Either
import com.example.sample.core.CommonEffectHandler.Effect
import com.example.sample.core.Eff
import com.example.sample.core.Reducer
import com.example.sample.core.map
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

val movieReducer = object : Reducer<State, Action, Effect<Action>> {
    override fun invoke(state: State, action: Action) =
        when (action) {
            is Action.LoadAction ->
                state.copy(loading = true, data = null, error = null) to
                        setOf(Effect(Domain.mkWebEffect(action.page)))
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

object Domain {

    fun mkWebEffect(page: Int): Eff<Action> {
        val request = Request.Builder()
            .url(
                HttpUrl.Builder()
                    .addQueryParameter("sort_by", "popularity.desc")
                    .addQueryParameter("page", page.toString())
                    .build()
            )
            .build()
        val webEffect = Effects.WebEffect(request, Response.serializer())
        return webEffect.map { handleResponse(it) }
    }

    fun handleResponse(result: Either<Exception, Response>): Action = when (result) {
        is Either.Left -> Action.ShowError(result.value)
        is Either.Right -> Action.ShowResult(result.value.results)
    }
}

// TODO: move to application-effects.kt

object Effects {
    lateinit var movieClient: OkHttpClient
    lateinit var baseUrl: HttpUrl

    class WebEffect<T>(val request: Request, val serializer: DeserializationStrategy<T>) : Eff<T> {
        override suspend fun invoke(): T =
            Json.nonstrict.parse(serializer, movieClient.newCall(request).execute().body!!.string())
    }
}
