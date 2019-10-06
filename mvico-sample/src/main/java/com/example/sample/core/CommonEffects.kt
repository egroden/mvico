package com.example.sample.core

import com.example.sample.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface Eff<T> {
    suspend fun invoke(): T
}

class CommonEffectHandler<Action> : EffectHandler<CommonEffectHandler.Effect<Action>, Action> {

    override fun handle(sideEffect: Effect<Action>): Flow<Action> =
        flow { emit(sideEffect.e.invoke()) }

    class Effect<Action>(val e: Eff<Action>)
}

fun <T, R> Eff<T>.map(f: (Either<Exception, T>) -> R): Eff<R> = MapEffect(this, f)

class MapEffect<T, R>(val origin: Eff<T>, val f: (Either<Exception, T>) -> R) : Eff<R> {
    override suspend fun invoke(): R =
        try {
            Either.Right(origin.invoke())
        } catch (e: Exception) {
            Either.Left(e)
        }.let { f(it) }
}
