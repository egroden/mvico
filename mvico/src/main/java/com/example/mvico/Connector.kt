package com.example.mvico

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class Connector<Action, SideEffect, State, Subscription>(
    private val renderScope: CoroutineScope,
    private val feature: Feature<Action, SideEffect, State, Subscription>
) {
    infix fun bindAction(action: Action) {
        feature.featureScope.launch { feature.actions.offer(action) }
    }

    fun connect(render: Render<State>) {
        renderScope.launch {
            feature.states.consumeEach(render)
        }
    }

    fun disconnect() = renderScope.cancel()
}
