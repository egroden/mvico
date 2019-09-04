package com.example.mvi.core

typealias Reducer<State, Action, Command> = (State, Action) -> Pair<State, Set<Command>>

typealias Render<State> = (State) -> Unit