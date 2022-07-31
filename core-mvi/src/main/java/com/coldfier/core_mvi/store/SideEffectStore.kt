package com.coldfier.core_mvi.store

import com.coldfier.core_mvi.Middleware
import com.coldfier.core_mvi.Reducer
import com.coldfier.core_mvi.SideEffectProducer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

abstract class SideEffectStore<State: Any, UiEvent: Any, SideEffect: Any> : Store<State, UiEvent>() {

    protected abstract val reducer: Reducer<State, UiEvent>
    protected abstract val middleware: Middleware<State, UiEvent, UiEvent>
    protected abstract val sideEffectProducer: SideEffectProducer<State, UiEvent, SideEffect>

    private val sideEffectChannel = Channel<SideEffect>()
    val sideEffectFlow: Flow<SideEffect>
        get() = sideEffectChannel.receiveAsFlow().flowOn(Dispatchers.Main.immediate)

    init {
        storeCoroutineScope.launch {
            eventSharedFlow.collect { event ->

                sideEffectProducer(_stateFlow.value, event)?.let { effect ->
                    sideEffectChannel.send(effect)
                }

                _stateFlow.update { reducer(_stateFlow.value, event) }

                middleware(_stateFlow.value, event)
                    ?.flowOn(Dispatchers.IO)
                    ?.onEach { middlewareEvent -> consumeEvent(middlewareEvent) }
                    ?.collect()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        middleware.onCleared()
    }
}