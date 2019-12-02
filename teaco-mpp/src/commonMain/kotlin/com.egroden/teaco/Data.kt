package com.egroden.teaco

import kotlinx.coroutines.flow.Flow

data class UpdateResponse<State, Subscription, SideEffect>(
    val state: State,
    val subscription: Subscription? = null,
    val sideEffects: Set<SideEffect> = emptySet()
)

/**
 * Updates the current application state and provides a way to schedule the execution
 * of a side effect.
 *
 * @param State Current application state.
 * @param Action Incoming action.
 *
 * @return new state and a side effect to handle. If you don't wish to handle
 * a side-effect, return Pair<State, emptySet()> , otherwise return Pair<State, setOf()>
 */
typealias Updater<State, Action, Subscription, SideEffect> =
            (State, Action) -> UpdateResponse<State, Subscription, SideEffect>

/**
 * Called every time the MviFeature produces a new state. Used to display
 * the state in the UI
 */
typealias Render<T> = (T) -> Unit

/**
 * Your business logic should be here.
 * @param SideEffect Type of side effects.
 * @param Action Type of actions with which the state will change.
 */
typealias EffectHandler<SideEffect, Action> = (SideEffect) -> Flow<Action>