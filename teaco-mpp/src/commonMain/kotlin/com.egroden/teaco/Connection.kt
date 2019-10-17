package com.egroden.teaco

import kotlinx.coroutines.channels.ReceiveChannel

class Connection<State, Subscription>(
    statusReceiveChannel: ReceiveChannel<State>,
    subscriptionReceiveChannel: ReceiveChannel<Subscription>
) {
    private var statusReceiveChannel: ReceiveChannel<State>? =
        statusReceiveChannel
    private var subscriptionReceiveChannel: ReceiveChannel<Subscription>? =
        subscriptionReceiveChannel

    fun cancel() {
        statusReceiveChannel?.cancel()
        statusReceiveChannel = null
        subscriptionReceiveChannel?.cancel()
        subscriptionReceiveChannel = null
    }
}