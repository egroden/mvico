package com.example.mvico

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

typealias Eff<T> = () -> T

class CommonEffectHandler<Action> : EffectHandler<CommonEffectHandler.Effect<Action>, Action> {
    override fun invoke(sideEffect: Effect<Action>): Flow<Action> =
        flow { emit(sideEffect.e.invoke()) }

    class Effect<Action>(val e: Eff<Action>)
}

fun <T, R> Eff<T>.map(f: (Either<Exception, T>) -> R): Eff<R> = MapEffect(this, f)

class MapEffect<T, R>(private val origin: Eff<T>, private val f: (Either<Exception, T>) -> R) : Eff<R> {
    override fun invoke(): R =
        try {
            Either.Right(origin.invoke())
        } catch (e: Exception) {
            Either.Left(e)
        }.let { f(it) }
}
