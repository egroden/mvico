package com.example.mvi.core

typealias Reducer<State, Action, Command> = (State, Action) -> Pair<State, Set<Command>>

typealias Render<State> = (State) -> Unit

sealed class Either<out L, out R> {
    data class Left<T>(val value: T) : Either<T, Nothing>()
    data class Right<T>(val value: T) : Either<Nothing, T>()
}