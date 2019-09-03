package com.example.mvi.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect

class MviFeature<Action, Command, State, Subscription>(
    private val initialState: State,
    private val reduce: Reducer<State, Action, Command>,
    private val commandHandler: CommandHandler<Command, Action>
) : Feature<Action, Command, State, Subscription> {

    override val currentState: State
        get() = states.value

    override val actions: Channel<Action>
        get() = Channel(Channel.CONFLATED)

    override val states: ConflatedBroadcastChannel<State>
        get() = ConflatedBroadcastChannel(initialState)

    override val featureScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    override val renderScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main) + SupervisorJob()

    init {
        featureScope.launch {
            actions.consumeEach { action ->
                val computation  = reduce(currentState, action)
                states.send(computation.state)
                when(computation){
                    is Pure -> {}
                    is Effect -> computation.commands.forEach(::call)
                }
            }
        }
    }

    override fun call(command: Command) {
        featureScope.launch {
            commandHandler.handle(command).collect{ actions.offer(it) }
        }
    }
}
