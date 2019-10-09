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
    initialState: State,
    update: Updater<State, Action, SideEffect>,
    effectHandler: EffectHandler<SideEffect, Action>,
    onError: ((State, Throwable) -> Unit)? = null,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val connector: Connector<Action, SideEffect, State, Subscription>
    private val stateKey = "android_connector"

    init {
        val defState = savedStateHandle.get<State>(stateKey) ?: initialState
        connector = Connector(
            renderScope = viewModelScope,
            feature = MviFeature(defState, update, effectHandler, onError)
        )
        viewModelScope.launch {
            connector.feature.states.consumeEach {
                savedStateHandle.set(stateKey, it)
            }
        }
    }

    infix fun bindAction(action: Action) =
        connector bindAction action

    fun connect(render: Render<State>, lifecycle: Lifecycle) {
        lifecycle.addObserver(
            LifecycleEventObserver { _, event: Lifecycle.Event ->
                if (event == Lifecycle.Event.ON_START)
                    connector.connect(render)
                else if (event == Lifecycle.Event.ON_STOP)
                    connector.disconnect()
            }
        )
    }

    class Factory<Action, SideEffect, State, Subscription>(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null,
        private val init: (handle: SavedStateHandle) -> AndroidConnector<Action, SideEffect, State, Subscription>
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T =
            init(handle) as T
    }
}

fun <Action, SideEffect, State, Subscription> Fragment.androidConnectors(
    init: (SavedStateHandle) -> AndroidConnector<Action, SideEffect, State, Subscription>
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, init) }

fun <Action, SideEffect, State, Subscription> ComponentActivity.androidConnectors(
    init: (SavedStateHandle) -> AndroidConnector<Action, SideEffect, State, Subscription>
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, init) }