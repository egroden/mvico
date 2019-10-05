package com.egroden.mvico

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * The main component of architecture.
 * @param Action Type of actions with which the state will change.
 * @param SideEffect Type of side effects.
 * @param State Type of state.
 * @param Subscription Type for one-time messages.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
interface Feature<in Action, SideEffect, out State, out Subscription> {
    /**
     * Actions channel. Used to respond to new actions.
     */
    val actions: Channel<in Action>

    /**
     * Current state of Feature.
     */
    val currentState: State

    /**
     * States channel. Used to notify of a state change.
     */
    val states: ConflatedBroadcastChannel<out State>

    /**
     * Scope for unidirectional data flow.
     * Processing actions and commands occurs in this scope.
     */
    val featureScope: CoroutineScope

    /**
     * Handles Side Effect.
     * @param sideEffect SideEffect to perform a side effect.
     * For example, download something from a network or database.
     */
    fun call(sideEffect: SideEffect)
}

