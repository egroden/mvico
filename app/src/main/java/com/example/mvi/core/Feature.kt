package com.example.mvi.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach

interface Feature<Action, Command, State, Subscription> {
    val actions: Channel<Action>
    val currentState: State
    val states: ConflatedBroadcastChannel<State>

    val featureScope: CoroutineScope
    val renderScope: CoroutineScope

    fun call(command: Command)
}


infix fun <Action, Command, State, Subscription> Feature<Action, Command, State, Subscription>.bindAction(action: Action){
    featureScope.launch {
        actions.offer(action)
    }
}

fun <Action, Command, State, Subscription> Feature<Action, Command, State, Subscription>.bind(render: Render<State>){
    renderScope.launch {
        states.openSubscription().consumeEach(render)
    }
}

fun <Action, Command, State, Subscription> Feature<Action, Command, State, Subscription>.unbind(){
    renderScope.cancel()
}

