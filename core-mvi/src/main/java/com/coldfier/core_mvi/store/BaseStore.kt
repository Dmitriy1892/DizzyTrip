package com.coldfier.core_mvi.store

import com.coldfier.core_mvi.Middleware
import com.coldfier.core_mvi.Reducer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class BaseStore<State: Any, UiEvent: Any> : Store<State, UiEvent>() {

    protected abstract val reducer: Reducer<State, UiEvent>
    protected abstract val middleware: Middleware<State, UiEvent, UiEvent>

    init {
        storeCoroutineScope.launch {
            eventSharedFlow.collect { event ->
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