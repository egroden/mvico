package com.egroden.teaco

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Channel<T>.asFlow(): Flow<T> = flow {
    for (elem in this@asFlow) emit(elem)
}