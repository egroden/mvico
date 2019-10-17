package com.egroden.teaco

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
interface Feature<Action, SideEffect, State, Subscription> {
    /**
     * Default state for [states].
     */
    val initialState: State

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
     * Channel of single actions. Used for one-time event.
     */
    val subscriptions: ConflatedBroadcastChannel<out Subscription>

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

