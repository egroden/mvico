package com.example.mvico

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

    //TODO: choose capacity
    override val actions = Channel<Action>(Channel.CONFLATED)

    override val states = ConflatedBroadcastChannel(initialState)

    override val featureScope = CoroutineScope(Dispatchers.IO) + SupervisorJob() + exceptionHandler

    override val renderScope = CoroutineScope(Dispatchers.Main) + SupervisorJob()


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
            effectHandler.handle(sideEffect).collect{ actions.offer(it) }
        }
    }
}
