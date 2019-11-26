package com.egroden.teaco

import kotlinx.coroutines.MainScope

class IosConnector<Action, SideEffect, State, Subscription>(
    feature: Feature<Action, SideEffect, State, Subscription>
) {
    private val connector = Connector(MainScope(), feature)

    fun bindAction(action: Action) =
        connector bindAction action
}