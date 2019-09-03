package com.example.mvi.core

import kotlinx.coroutines.flow.Flow

interface CommandHandler<Command, Action>{
    fun handle(command: Command): Flow<Action>
}