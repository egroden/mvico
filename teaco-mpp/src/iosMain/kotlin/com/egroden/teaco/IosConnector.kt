package com.egroden.teaco

import kotlinx.coroutines.MainScope

class IosConnector<Action, SideEffect, State, Subscription>(
    feature: Feature<Action, SideEffect, State, Subscription>
) {
    private val connector = Connector(MainScope(), feature)

    fun connect(
        renderState: Render<State>,
        renderSubscription: Render<Subscription>
    ) =
        connector.connect(renderState, renderSubscription)

    fun bindAction(action: Action) =
        connector bindAction action
}