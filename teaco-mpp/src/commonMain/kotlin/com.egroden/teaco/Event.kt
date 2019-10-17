package com.egroden.teaco

import kotlin.jvm.Volatile

sealed class Event<T> {
    class OneTime<T>(val value: T) : Event<T>()

    class Reusable<T>(internal val value: T) : Event<T>() {
        @Volatile
        internal var pending = true
    }

    fun getIfNotUsed(block: (T) -> Unit) {
        when (this) {
            is Reusable<T> -> if (pending) block(value)
            is OneTime<T> -> block(value)
        }
    }

    fun markAsUsed() {
        if (this is Reusable<T>)
            pending = false
    }
}



