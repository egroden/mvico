package com.example.mvico

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach

/**
 * @param initialState Initial state for Feature.
 * @param reduce Function for updating state and creating side effects.
 * @param commandExecutor Side effects handler
 * @param onError Function for handling unhandled exceptions.
 */
class MviFeature<Action, Command, State, Subscription>(
    initialState: State,
    private val reduce: com.example.mvico.Reducer<State, Action, Command>,
    private val commandExecutor: com.example.mvico.CommandExecutor<Command, Action>,
    private val onError: ((State, Throwable) -> Unit)? = null
) : com.example.mvico.Feature<Action, Command, State, Subscription> {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError?.invoke(currentState, throwable)
    }

    override val currentState: State
        get() = states.value

    override val actions = Channel<Action>(Channel.CONFLATED)

    override val states = ConflatedBroadcastChannel(initialState)

    override val featureScope = CoroutineScope(Dispatchers.IO) + SupervisorJob() + exceptionHandler

    override val renderScope = CoroutineScope(Dispatchers.Main) + SupervisorJob()


    init {
        featureScope.launch {
            actions.consumeEach { action ->
                val (state, commands) = reduce(currentState, action)
                states.send(state)
                commands.forEach(::call)
            }
        }
    }

    override fun call(command: Command) {
        featureScope.launch {
            commandExecutor.execute(command).collect{ actions.offer(it) }
        }
    }
}
