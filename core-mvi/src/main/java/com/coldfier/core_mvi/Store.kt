package com.coldfier.core_mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable

abstract class Store<Action: Any, State: Any, SideEffect: Any> : Closeable {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    protected abstract val reducer: Reducer<State, Action>
    protected abstract val middlewares: List<Middleware<Action>>

    protected abstract val _stateFlow: MutableStateFlow<State>
    val stateFlow: StateFlow<State>
        get() = _stateFlow.asStateFlow()

    protected abstract val actionSharedFlow: MutableSharedFlow<Action>

    init {
        coroutineScope.launch {
            actionSharedFlow.collect {

            }
        }
    }

    fun sendAction(action: Action) {
        coroutineScope.launch {
            actionSharedFlow.emit(action)
        }
    }

    override fun close() {
        coroutineScope.cancel()
    }
}