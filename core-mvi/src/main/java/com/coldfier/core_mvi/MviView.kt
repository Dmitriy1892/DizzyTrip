package com.coldfier.core_mvi

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface MviView<State: Any, SideEffect: Any> {

    val stateFlow: StateFlow<State>

    val sideEffectFlow: Flow<SideEffect>

    fun bindMviView(lifecycleOwner: LifecycleOwner) {
        Log.d(javaClass.simpleName, "MviView bind started")
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.onEach(::renderState).collect()
            }

            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sideEffectFlow.onEach(::renderSideEffect).collect()
            }
        }
    }

    fun renderState(state: State)

    fun renderSideEffect(sideEffect: SideEffect)
}