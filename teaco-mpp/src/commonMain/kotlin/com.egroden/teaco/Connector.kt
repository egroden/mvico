package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@UseExperimental(ObsoleteCoroutinesApi::class)
class Connector<Action, SideEffect, State, Subscription>(
    val renderScope: CoroutineScope,
    val feature: Feature<Action, SideEffect, State, Subscription>
) {
    var renderState: Render<State>? = null
    var renderSubscription: Render<Subscription>? = null

    init {
        renderScope.launch {
            feature.states.consumeEach {
                renderState?.invoke(it)
            }
        }
        renderScope.launch {
            feature.subscriptions.consumeEach {
                renderSubscription?.invoke(it)
            }
        }
    }
}

infix fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) {
    with(feature) {
        featureScope.launch { actions.send(action) }
    }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.connect(
    renderState: Render<State>,
    renderSubscription: Render<Subscription>
) {
    // subscribe on next updates
    this.renderState = renderState
    this.renderSubscription = renderSubscription

    // show current state
    feature.states.valueOrNull?.let { renderState(it) }
}

fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.disconnect() {
    renderState = null
    renderSubscription = null
}