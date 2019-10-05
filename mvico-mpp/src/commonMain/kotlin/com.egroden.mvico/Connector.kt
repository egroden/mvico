package com.egroden.mvico

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach

class Connector<Action, SideEffect, State, Subscription>(
    val feature: Feature<Action, SideEffect, State, Subscription>
) {
    val renderScope: CoroutineScope = MainScope()
}

infix fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) {
    with(feature) {
        featureScope.launch { actions.offer(action) }
    }
}

@UseExperimental(ObsoleteCoroutinesApi::class)
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