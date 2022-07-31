package com.coldfier.core_mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow

/**
 *  Middleware needs for long-running tasks, like hard calculation, network requests,
 *  database operations etc. It must contains a most part of business logic.
 */
abstract class Middleware<State, UiEvent, Action> {

    protected val middlewareCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    abstract operator fun invoke(state: State, event: UiEvent): Flow<Action>?

    fun onCleared() = middlewareCoroutineScope.cancel()
}