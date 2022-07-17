package com.coldfier.core_mvi

import kotlinx.coroutines.flow.Flow

interface Middleware<Action> {
    fun produceEffect(action: Action): Flow<Action>
}