package com.example.sample.core

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling side effects.
 * Your business logic should be here.
 * @param SideEffect Type of side effects.
 * @param Action Type of actions with which the state will change.
 */
interface EffectHandler<SideEffect, Action>{

    /**
     * @param sideEffect Side effect.
     * @return flow actions for updating state.
     */
    fun handle(sideEffect: SideEffect): Flow<Action>
}