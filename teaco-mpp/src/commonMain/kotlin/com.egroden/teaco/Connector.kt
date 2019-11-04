package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
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

@UseExperimental(FlowPreview::class)
fun <Action, SideEffect, State, Subscription> Connector<Action, SideEffect, State, Subscription>.connect(
    renderState: Render<State>,
    renderSubscription: Render<Subscription>
) = ConnectionJob(
    statusJob = renderScope.launch {
        feature.states
            .asFlow()
            .collect { renderState(it) }
    },
    subscriptionJob = renderScope.launch {
        feature.subscriptions
            .asFlow()
            .collect { renderSubscription(it) }
    }
)