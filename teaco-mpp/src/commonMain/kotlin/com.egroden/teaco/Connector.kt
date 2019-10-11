package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.jvm.JvmName

class Connector<Action, SideEffect, State, Subscription>(
    val renderScope: CoroutineScope,
    val feature: Feature<Action, SideEffect, State, Subscription>
)

infix fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) {
    with(feature) {
        featureScope.launch { actions.send(action) }
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

@UseExperimental(ObsoleteCoroutinesApi::class)
@JvmName("connectSubscription")
fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.connect(
    render: Render<Subscription>
) {
    renderScope.launch {
        feature.subscriptions.consumeEach(render)
    }
}

fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.disconnect() =
    renderScope.coroutineContext.cancelChildren()