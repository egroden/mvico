package com.example.sample.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class Connector<Action, SideEffect, State, Subscription>(val feature: Feature<Action, SideEffect, State, Subscription>) {
    val renderScope: CoroutineScope = MainScope()
}

infix fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) {
    with(feature) {
        featureScope.launch { actions.offer(action) }
    }
}

fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.connect(
    render: Render<State>
) {
    renderScope.launch {
        feature.states.consumeEach(render)
    }
}

fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.disconnect() {
    renderScope.cancel()
}