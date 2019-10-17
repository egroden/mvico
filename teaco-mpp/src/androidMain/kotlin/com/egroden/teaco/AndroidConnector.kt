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
    teaFeature: TeaFeature<Action, SideEffect, State, Subscription>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)?,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val connector: Connector<Action, SideEffect, State, Subscription>

    private val stateKey = "android_connector"

    init {
        connector = Connector(
            renderScope = viewModelScope,
            feature = teaFeature.copy(
                initialState = savedStateHandle.get<State>(stateKey) ?: teaFeature.initialState
            )
        )
        viewModelScope.launch {
            connector.feature.statuses.consumeEach {
                savedStateHandle.set(stateKey, it)
            }
        }
        onFirstStart?.invoke(this)
    }

    class Factory<Action, SideEffect, State, Subscription>(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null,
        private val feature: TeaFeature<Action, SideEffect, State, Subscription>,
        private val onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)?
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T =
            AndroidConnector(feature, onFirstStart, handle) as T
    }
}


infix fun <Action, SideEffect, State, Subscription> AndroidConnector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) =
    connector bindAction action

fun <Action, SideEffect, State, Subscription> AndroidConnector<Action, SideEffect, State, Subscription>.connect(
    renderState: Render<State>,
    renderSubscription: Render<Event<Subscription>>,
    lifecycle: Lifecycle
) {
    lifecycle.addObserver(
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START)
                connector.connect(renderState, renderSubscription)
            else if (event == Lifecycle.Event.ON_STOP)
                connector.disconnect()
        }
    )
}

fun <Action, SideEffect, State, Subscription> Fragment.androidConnectors(
    feature: TeaFeature<Action, SideEffect, State, Subscription>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)? = null
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, feature, onFirstStart) }

fun <Action, SideEffect, State, Subscription> ComponentActivity.androidConnectors(
    teaFeature: TeaFeature<Action, SideEffect, State, Subscription>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)? = null
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, teaFeature, onFirstStart) }