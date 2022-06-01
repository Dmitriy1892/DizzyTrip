package com.coldfier.core_utils.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

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

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun EditText.setAfterTextChangedListenerWithDebounce(
    debounceMillis: Long = 300L,
    coroutineScope: CoroutineScope,
    actionBeforeDebounce: (suspend () -> Unit)? = null,
    actionAfterDebounce: suspend (text: String) -> Unit
): Job {
    val flow = callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }

    return flow
        .onEach { actionBeforeDebounce?.invoke() }
        .debounce(debounceMillis)
        .map { it?.toString() }
        .onEach { actionAfterDebounce(it ?: "") }
        .launchIn(coroutineScope)
}
