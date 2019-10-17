package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
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
    val status: ReceiveChannel<State> = feature.states.openSubscription()
    val subscription: ReceiveChannel<Subscription> = feature.subscriptions.openSubscription()

    renderScope.launch {
        status.consumeEach(renderState::invoke)
    }
    renderScope.launch {
        subscription.consumeEach(renderSubscription::invoke)
    }

    statusReceiveChannel = status
    subscriptionReceiveChannel = subscription
}

fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.disconnect() {
    statusReceiveChannel?.cancel()
    statusReceiveChannel = null
    subscriptionReceiveChannel?.cancel()
    subscriptionReceiveChannel = null
}