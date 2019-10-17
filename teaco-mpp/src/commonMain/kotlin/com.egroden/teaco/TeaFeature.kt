package com.egroden.teaco

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect

/**
 * @param initialState Initial state for Feature.
 * @param update Function for updating state and creating side effects.
 * @param effectHandler Side effects handler
 * @param onError Function for handling unhandled exceptions.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
data class TeaFeature<Action, SideEffect, State, Subscription>(
    override val initialState: State,
    private val update: Updater<State, Action, Subscription, SideEffect>,
    private val effectHandler: EffectHandler<SideEffect, Action>,
    private val onError: ((State, Throwable) -> Unit)? = null
) : Feature<Action, SideEffect, State, Subscription> {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError?.invoke(currentState, throwable)
    }

    override val currentState: State
        get() = statuses.value

    override val actions = Channel<Action>()

    override val statuses = ConflatedBroadcastChannel(initialState)

    override val subscriptions = ConflatedBroadcastChannel<Subscription>()

    override val featureScope =
        CoroutineScope(Dispatchers.Default) + SupervisorJob() + exceptionHandler

    init {
        featureScope.launch {
            actions.consumeEach { action ->
                val (state, subscription, sideEffects) = update(currentState, action)
                statuses.send(state)
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
}