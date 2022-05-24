package com.coldfier.core_utils.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun Fragment.launchCoroutineWithLifecycle(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline block: suspend () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch(dispatcher) {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

inline fun AppCompatActivity.launchCoroutineWithLifecycle(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline block: suspend () -> Unit
) {
    lifecycleScope.launch(dispatcher) {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

inline fun ViewModel.launchInIOCoroutine(crossinline block: suspend () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        block()
    }
}