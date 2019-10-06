package com.example.mvico_android

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.mvico.Connector
import com.example.mvico.Render

class AndroidConnector<Action, SideEffect, State, Subscription>(private val connector: Connector<Action, SideEffect, State, Subscription>) {
    infix fun bindAction(action: Action) = connector bindAction action

    fun connect(render: Render<State>) = connector.connect(render)

    fun disconnect() = connector.disconnect()
}

infix fun <Action, SideEffect, State, Subscription> Fragment.bindConnector(pair: Pair<AndroidConnector<Action, SideEffect, State, Subscription>, Render<State>>){
    val (androidConnector, render) = pair
    lifecycle.addObserver(
        object : LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() = androidConnector.connect(render)

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() = androidConnector.disconnect()
        }
    )
}