package com.egroden.mvico

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling side effects.
 * @param SideEffect Type of side effects.
 * @param Action Type of actions with which the state will change.
 */
interface EffectHandler<SideEffect, Action> {
    /**
     * @return flow actions for updating state.
     */
    fun handle(sideEffect: SideEffect): Flow<Action>
}