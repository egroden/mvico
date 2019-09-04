package com.example.mvi.core

import kotlinx.coroutines.flow.Flow

interface CommandExecutor<Command, Action>{
    fun execute(command: Command): Flow<Action>
}