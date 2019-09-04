package com.example.sample

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach

/**
 * The main component of architecture.
 * @param Action Type of actions with which the state will change.
 * @param Command Type of side effects.
 * @param State Type of state.
 * @param Subscription Type for one-time messages.
 */
interface Feature<in Action, Command, out State, out Subscription> {

    /**
     * Actions flow. Used to respond to new actions.
     */
    val actions: Channel<in Action>

    /**
     * Current state of Feature.
     */
    val currentState: State

    /**
     * States flow. Used to notify of a state change.
     */
    val states: ConflatedBroadcastChannel<out State>

    /**
     * Scope for unidirectional data flow.
     * Processing actions and commands occurs in this scope.
     */
    val featureScope: CoroutineScope

    /**
     * Scope for update ui.
     * TODO: Move control of scope to another place
     */
    val renderScope: CoroutineScope

    /**
     * Handles Side Effect.
     * @param command Command to perform a side effect.
     * For example, download something from a network or database
     */
    fun call(command: Command)
}

infix fun <Action, Command, State, Subscription> Feature<Action, Command, State, Subscription>.bindAction(action: Action){
    featureScope.launch { actions.offer(action) }
}

fun <Action, Command, State, Subscription> Feature<Action, Command, State, Subscription>.bind(render: Render<State>){
    renderScope.launch {
        states.openSubscription().consumeEach(render)
    }
}

fun <Action, Command, State, Subscription> Feature<Action, Command, State, Subscription>.unbind(){
    renderScope.cancel()
}

