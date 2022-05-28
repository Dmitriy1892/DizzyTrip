package com.coldfier.core_utils.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun ViewModel.launchInIOCoroutine(crossinline block: suspend () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        block()
    }
}

context (Fragment)
fun <T> Flow<T>.observeInCoroutine(block: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeInCoroutine.collect {
                block(it)
            }
        }
    }
}

context (AppCompatActivity)
fun <T> Flow<T>.observeInCoroutine(block: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeInCoroutine.collect {
                block(it)
            }
        }
    }
}