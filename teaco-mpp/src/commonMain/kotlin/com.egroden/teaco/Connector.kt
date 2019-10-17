package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class Connector<Action, SideEffect, State, Subscription>(
    val renderScope: CoroutineScope,
    val feature: Feature<Action, SideEffect, State, Subscription>
) {
    companion object

    var statusReceiveChannel: ReceiveChannel<State>? = null
    var subscriptionReceiveChannel: ReceiveChannel<Subscription>? = null
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
    renderScope.launch {
        feature.statuses
            .openSubscription()
            .also {
                statusReceiveChannel = it
            }
            .consumeAsFlow()
            .distinctUntilChanged()
            .collect { renderState(it) }
    }
    renderScope.launch {
        feature.subscriptions
            .openSubscription()
            .also {
                it.poll()
                subscriptionReceiveChannel = it
            }
            .consumeAsFlow()
            .distinctUntilChanged()
            .collect { renderSubscription(it) }
    }
}

fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.disconnect() {
    statusReceiveChannel?.cancel()
    statusReceiveChannel = null
    subscriptionReceiveChannel?.cancel()
    subscriptionReceiveChannel = null
}