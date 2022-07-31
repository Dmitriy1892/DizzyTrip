package com.coldfier.core_mvi.store

import com.coldfier.core_mvi.Middleware
import com.coldfier.core_mvi.Reducer
import com.coldfier.core_mvi.SideEffectProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class FullStore<State: Any, UiEvent: Any, Action: Any, SideEffect: Any>
    : Store<State, UiEvent>() {

    protected abstract val middleware: Middleware<State, UiEvent, Action>
    protected abstract val reducer: Reducer<State, Action>
    protected abstract val sideEffectProducer: SideEffectProducer<State, Action, SideEffect>

    protected val actionSharedFlow = MutableSharedFlow<Action>()

    private val sideEffectChannel = Channel<SideEffect>()
    val sideEffectFlow: Flow<SideEffect>
        get() = sideEffectChannel.receiveAsFlow().flowOn(Dispatchers.Main.immediate)

    init {
        storeCoroutineScope.launch {
            eventSharedFlow.collect { event ->
                middleware(_stateFlow.value, event)
                    ?.flowOn(Dispatchers.IO)
                    ?.onEach { action ->
                        actionSharedFlow.emit(action)
                    }
                    ?.collect()
            }
        }

        storeCoroutineScope.launch {
            actionSharedFlow.collect { action ->
                _stateFlow.update { reducer(_stateFlow.value, action) }
                sideEffectProducer(_stateFlow.value, action)?.let { effect ->
                    sideEffectChannel.send(effect)
                }
            }
        }
    }

    override fun consumeEvent(event: UiEvent) {
        storeCoroutineScope.launch {
            eventSharedFlow.emit(event)
            mapEventToAction(event)?.let { actionSharedFlow.emit(it) }
        }
    }

    protected abstract fun mapEventToAction(event: UiEvent): Action?

    override fun onCleared() {
        super.onCleared()
        middleware.onCleared()
    }
}