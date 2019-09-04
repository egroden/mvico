package com.example.mvi.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect

class MviFeature<Action, Command, State, Subscription>(
    initialState: State,
    private val reduce: Reducer<State, Action, Command>,
    private val commandExecutor: CommandExecutor<Command, Action>
) : Feature<Action, Command, State, Subscription> {

    override val currentState: State
        get() = states.value

    override val actions = Channel<Action>(Channel.CONFLATED)

    override val states = ConflatedBroadcastChannel(initialState)

    override val featureScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

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
