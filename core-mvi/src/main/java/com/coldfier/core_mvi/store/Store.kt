package com.coldfier.core_mvi.store

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Store<State: Any, UiEvent: Any> {

    protected abstract val _stateFlow: MutableStateFlow<State>
    val stateFlow: StateFlow<State>
        get() = _stateFlow.asStateFlow()

    protected val eventSharedFlow = MutableSharedFlow<UiEvent>()

    protected val storeCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    open fun consumeEvent(event: UiEvent) {
        storeCoroutineScope.launch { eventSharedFlow.emit(event) }
    }

    open fun onCleared() = storeCoroutineScope.cancel()
}