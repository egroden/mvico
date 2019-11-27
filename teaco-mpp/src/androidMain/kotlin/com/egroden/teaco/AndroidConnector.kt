package com.egroden.teaco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@UseExperimental(ObsoleteCoroutinesApi::class)
class AndroidConnector<Action, SideEffect, State, Subscription>(
    featureFactory: (oldState: State?) -> Feature<Action, SideEffect, State, Subscription>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)?,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val connector: Connector<Action, SideEffect, State, Subscription>

    private val stateKey = "android_connector"

    init {
        val feature = featureFactory(savedStateHandle.get<State>(stateKey))
        connector = Connector(renderScope = viewModelScope, feature = feature)
        viewModelScope.launch {
            feature.states.consumeEach {
                savedStateHandle.set(stateKey, it)
            }
        }
        onFirstStart?.invoke(this)
    }

    override fun onCleared() {
        super.onCleared()
        connector.feature.cancel()
    }

    class Factory<Action, SideEffect, State, Subscription>(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null,
        private val featureFactory: (oldState: State?) -> Feature<Action, SideEffect, State, Subscription>,
        private val onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)?
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T =
            AndroidConnector(featureFactory, onFirstStart, handle) as T
    }
}


infix fun <Action, SideEffect, State, Subscription> AndroidConnector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) =
    connector bindAction action

fun <Action, SideEffect, State, Subscription> AndroidConnector<Action, SideEffect, State, Subscription>.connect(
    render: Render<State, Subscription>,
    lifecycle: Lifecycle
) {
    var connectionJob: ConnectionJob? = null
    lifecycle.addObserver(
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                connectionJob = connector.connect(render)
            } else if (event == Lifecycle.Event.ON_STOP) {
                connectionJob?.cancel()
                connectionJob = null
            }
        }
    )
}

fun <Action, SideEffect, State, Subscription> Fragment.androidConnectors(
    featureFactory: (oldState: State?) -> Feature<Action, SideEffect, State, Subscription>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)? = null
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels {
        AndroidConnector.Factory(this, null, featureFactory, onFirstStart)
    }

fun <Action, SideEffect, State, Subscription> ComponentActivity.androidConnectors(
    featureFactory: (oldState: State?) -> Feature<Action, SideEffect, State, Subscription>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)? = null
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, featureFactory, onFirstStart) }