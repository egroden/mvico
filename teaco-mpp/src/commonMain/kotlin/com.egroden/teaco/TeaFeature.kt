package com.egroden.teaco

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect

/**
 * @param initialState Initial state for Feature.
 * @param updater Function for updating state and creating side effects.
 * @param effectHandler Side effects handler
 * @param onError Function for handling unhandled exceptions.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
data class TeaFeature<Action, SideEffect, State, Subscription>(
    override val initialState: State,
    private val updater: Updater<State, Action, Subscription, SideEffect>,
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

    override val subscriptions = Channel<Subscription>()

    override val featureScope =
        CoroutineScope(Dispatchers.Default) + SupervisorJob() + exceptionHandler

    init {
        featureScope.launch {
            actions.consumeEach { action ->
                val (state, subscription, sideEffects) = updater.invoke(currentState, action)
                states.send(state)
                subscription?.let { subscriptions.send(it) }
                sideEffects.forEach(::call)
            }
        }
    }

    override fun call(sideEffect: SideEffect) {
        featureScope.launch {
            effectHandler
                .invoke(sideEffect)
                .collect { actions.send(it) }
        }
    }

    override fun cancel() {
        featureScope.cancel()
        actions.cancel()
        states.cancel()
        subscriptions.cancel()
    }
}