package com.egroden.mvico

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach

class Connector<Action, SideEffect, State, Subscription>(
    val feature: Feature<Action, SideEffect, State, Subscription>
) {
    val renderScope: CoroutineScope = MainScope()

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

    fun disconnect() {
        renderScope.coroutineContext.cancelChildren()
    }
}
