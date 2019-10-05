package com.egroden.mvico

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect

/**
 * @param initialState Initial state for Feature.
 * @param reduce Function for updating state and creating side effects.
 * @param effectHandler Side effects handler
 * @param onError Function for handling unhandled exceptions.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
class MviFeature<Action, SideEffect, State, Subscription>(
    initialState: State,
    private val reduce: Reducer<State, Action, SideEffect>,
    private val effectHandler: EffectHandler<SideEffect, Action>,
    private val onError: ((State, Throwable) -> Unit)? = null
) : Feature<Action, SideEffect, State, Subscription> {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError?.invoke(currentState, throwable)
    }

    override val currentState: State
        get() = states.value

    override val actions = Channel<Action>()

    override val states = ConflatedBroadcastChannel(initialState)

    override val featureScope =
        CoroutineScope(Dispatchers.Default) + SupervisorJob() + exceptionHandler

    init {
        featureScope.launch {
            actions.consumeEach { action ->
                val (state, sideEffects) = reduce(currentState, action)
                states.send(state)
                sideEffects.forEach(::call)
            }
        }
    }

    override fun call(sideEffect: SideEffect) {
        featureScope.launch {
            effectHandler
                .handle(sideEffect)
                .collect { actions.send(it) }
        }
    }
}
