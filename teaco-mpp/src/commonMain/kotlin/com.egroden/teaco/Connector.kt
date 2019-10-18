package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
): ConnectionJob {
    val statesFlow = feature.states
        .openSubscription()
        .consumeAsFlow()
        .distinctUntilChanged()

    val subscriptionsFlow = feature.subscriptions
        .consumeAsFlow()
        .distinctUntilChanged()

    return ConnectionJob(
        statusJob = renderScope.launch { statesFlow.collect { renderState(it) } },
        subscriptionJob = renderScope.launch { subscriptionsFlow.collect { renderSubscription(it) } }
    )
}