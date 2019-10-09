package com.egroden.teaco

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class Connector<Action, SideEffect, State, Subscription>(
    val renderScope: CoroutineScope,
    val feature: Feature<Action, SideEffect, State, Subscription>
) {
    infix fun bindAction(action: Action) {
        with(feature) {
            featureScope.launch { actions.send(action) }
        }
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    fun connect(render: Render<State>) {
        renderScope.launch {
            feature.states.consumeEach(render)
        }
    }

    fun disconnect() =
        renderScope.coroutineContext.cancelChildren()
}
