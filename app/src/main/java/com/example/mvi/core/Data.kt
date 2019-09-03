package com.example.mvi.core

typealias Reducer<State, Action, Command> = (State, Action) -> Pair<State, Set<Command>>

typealias Render<State> = (State) -> Unit

sealed class Either<out L, out R> {
    data class Left<T>(val value: T) : Either<T, Nothing>()
    data class Right<T>(val value: T) : Either<Nothing, T>()
}

inline fun <T> either(f: () -> T): Either<Exception, T> =
    try {
        Either.Right(f())
    } catch (e: Exception) {
        Either.Left(e)
    }

suspend fun <T> either(f: suspend () -> T): Either<Exception, T> =
    try {
        Either.Right(f())
    } catch (e: Exception) {
        Either.Left(e)
    }

inline infix fun <A, B, C> Either<A, B>.map(f: (B) -> C): Either<A, C> = when(this) {
    is Either.Left -> this
    is Either.Right -> Either.Right(f(this.value))
}

inline infix fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = when(this) {
    is Either.Left -> this
    is Either.Right -> f(value)
}

inline infix fun <A, B, C> Either<A, C>.mapError(f: (A) -> B): Either<B, C> = when(this) {
    is Either.Left -> Either.Left(f(value))
    is Either.Right -> this
}

inline fun <A, B, C> Either<A, B>.fold(left: (A) -> C, right: (B) -> C): C = when(this) {
    is Either.Left -> left(this.value)
    is Either.Right -> right(this.value)
}