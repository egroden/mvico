package com.example.mvi.core

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling side effects.
 * Your business logic should be here.
 * @param Command Type of side effects.
 * @param Action Type of actions with which the state will change.
 */
interface CommandExecutor<Command, Action>{

    /**
     * @param command Side effect.
     * @return flow actions for updating state.
     */
    fun execute(command: Command): Flow<Action>
}