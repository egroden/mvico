package com.egroden.teaco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@UseExperimental(ObsoleteCoroutinesApi::class)
class AndroidConnector<Action, SideEffect, State, Subscription>(
    teaFeature: TeaFeature<Action, SideEffect, State, Subscription>,
    stateParser: StateParser<State>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)?,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val connector: Connector<Action, SideEffect, State, Subscription>

    private val stateKey = "android_connector"
    private val stateToString: (State) -> String = stateParser.first
    private val stringToState: (String) -> State = stateParser.second

    init {
        connector = Connector(
            renderScope = viewModelScope,
            feature = teaFeature.copy(
                initialState = savedStateHandle
                    .get<String>(stateKey)
                    ?.let(stringToState::invoke)
                    ?: teaFeature.initialState
            )
        )
        viewModelScope.launch(Dispatchers.Default) {
            connector.feature.states.consumeEach {
                savedStateHandle.set(stateKey, stateToString(it))
            }
        }
        onFirstStart?.invoke(this)
    }

    class Factory<Action, SideEffect, State, Subscription>(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null,
        private val feature: TeaFeature<Action, SideEffect, State, Subscription>,
        private val stateParser: Pair<(State) -> String, (String) -> State>,
        private val onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)?
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T =
            AndroidConnector(feature, stateParser, onFirstStart, handle) as T
    }
}


infix fun <Action, SideEffect, State, Subscription> AndroidConnector<Action, SideEffect, State, Subscription>.bindAction(
    action: Action
) =
    connector bindAction action

fun <Action, SideEffect, State, Subscription> AndroidConnector<Action, SideEffect, State, Subscription>.connect(
    renderState: Render<State>,
    renderSubscription: Render<Subscription>
) {
    connector.connect(renderState)
    connector.connect(renderSubscription)
}

fun <Action, SideEffect, State, Subscription> Fragment.androidConnectors(
    feature: TeaFeature<Action, SideEffect, State, Subscription>,
    stateParser: StateParser<State>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)? = null
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, feature, stateParser, onFirstStart) }

fun <Action, SideEffect, State, Subscription> ComponentActivity.androidConnectors(
    teaFeature: TeaFeature<Action, SideEffect, State, Subscription>,
    stateParser: StateParser<State>,
    onFirstStart: (AndroidConnector<Action, SideEffect, State, Subscription>.() -> Unit)? = null
): Lazy<AndroidConnector<Action, SideEffect, State, Subscription>> =
    viewModels { AndroidConnector.Factory(this, null, teaFeature, stateParser, onFirstStart) }