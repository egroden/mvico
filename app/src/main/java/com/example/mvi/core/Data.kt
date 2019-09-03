package com.example.mvi.core


typealias Reducer<State, Action, Command> = (State, Action) -> Return<State, Command>

typealias Render<State> = (State) -> Unit

sealed class Return<out State, out Command> {
    abstract val state: State
}

data class Pure<State>(override val state: State) : Return<State, Nothing>()

data class Effect<State, Command>(override val state: State, val commands: Set<Command>) : Return<State, Command>()