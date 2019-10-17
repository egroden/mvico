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
): Connection<State, Subscription> {
    val statusReceiveChannel: ReceiveChannel<State>
    val subscriptionReceiveChannel: ReceiveChannel<Subscription>

    val statesFlow = feature.states
        .openSubscription()
        .also { statusReceiveChannel = it }
        .consumeAsFlow()
        .distinctUntilChanged()
    val subscriptionsFlow = feature.subscriptions
        .openSubscription()
        .also { subscriptionReceiveChannel = it }
        .consumeAsFlow()
        .distinctUntilChanged()

    renderScope.launch { statesFlow.collect { renderState(it) } }
    renderScope.launch { subscriptionsFlow.collect { renderSubscription(it) } }

    return Connection(statusReceiveChannel, subscriptionReceiveChannel)
}