package com.egroden.teaco

import kotlinx.coroutines.MainScope

class IosConnector<Action, SideEffect, State, Subscription>(
    feature: Feature<Action, SideEffect, State, Subscription>
) {
    private val connector = Connector(MainScope(), feature)

    fun connect(render: Render<State, Subscription>) =
        connector.connect(render)

    fun bindAction(action: Action) =
        connector bindAction action
}