package com.egroden.teaco

import kotlinx.coroutines.MainScope

class IosConnector<Action, SideEffect, State, Subscription>(
    feature: Feature<Action, SideEffect, State, Subscription>
) {
    val connector = Connector(MainScope(), feature)
}

fun <Action, SideEffect, State, Subscription> IosConnector<Action, SideEffect, State, Subscription>.connect(
    render: Render<State, Subscription>
) =
    connector.connect(render)

fun <Action, SideEffect, State, Subscription> IosConnector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) =
    connector bindAction action