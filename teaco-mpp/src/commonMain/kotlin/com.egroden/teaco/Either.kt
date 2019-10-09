package com.egroden.teaco

sealed class Either<out L, out R> {
    data class Left<T>(val value: T) : Either<T, Nothing>()
    data class Right<T>(val value: T) : Either<Nothing, T>()

    companion object
}

inline fun <reified A> identity(a: A): A = a

infix fun <L> Either.Companion.left(left: L): Either.Left<L> =
    Either.Left(left)

infix fun <R> Either.Companion.right(right: R): Either.Right<R> =
    Either.Right(right)

inline fun <T> either(f: () -> T): Either<Exception, T> =
    try {
        Either.Right(f())
    } catch (e: Exception) {
        Either.Left(e)
    }

fun <A, B> Either<A, B>.swap(): Either<B, A> =
    fold(Either.Companion::right, Either.Companion::left)

inline infix fun <A, B, C> Either<A, B>.map(f: (B) -> C): Either<A, C> = when (this) {
    is Either.Left -> this
    is Either.Right -> Either.Right(f(value))
}

inline infix fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> f(value)
    }

inline infix fun <A, B, C> Either<A, C>.mapLeft(f: (A) -> B): Either<B, C> = when (this) {
    is Either.Left -> Either.Left(f(value))
    is Either.Right -> this
}

inline fun <A, B, C, D> Either<A, B>.bimap(
    leftOperation: (A) -> C,
    rightOperation: (B) -> D
): Either<C, D> =
    mapLeft(leftOperation) map (rightOperation)

inline fun <A, B, C> Either<A, B>.fold(
    ifLeft: (A) -> C,
    ifRight: (B) -> C
): C = when (this) {
    is Either.Left -> ifLeft(value)
    is Either.Right -> ifRight(value)
}

inline infix fun <A, B> Either<A, B>.exists(predicate: (B) -> Boolean): Boolean =
    fold({ false }, predicate)

inline fun <reified A, reified B> Either<A, B>.getOrHandle(default: (A) -> B): B =
    fold(default, ::identity)

inline fun <A, B> Either<A, B>.filterOrOther(
    predicate: (B) -> Boolean,
    default: (B) -> A
): Either<A, B> =
    flatMap {
        if (predicate(it)) Either.Right(it)
        else Either.Left(default(it))
    }

inline infix fun <A, B> Either<A, B?>.leftIfNull(crossinline default: () -> A): Either<A, B> =
    flatMap { it.rightIfNotNull { default() } }

inline infix fun <A, B> B?.rightIfNotNull(default: () -> A): Either<A, B> = when (this) {
    null -> Either.Left(default())
    else -> Either.Right(this)
}

fun <A> A.left(): Either<A, Nothing> = Either.Left(this)

fun <A> A.right(): Either<Nothing, A> = Either.Right(this)