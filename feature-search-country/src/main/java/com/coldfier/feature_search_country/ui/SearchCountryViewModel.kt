package com.coldfier.feature_search_country.ui

import androidx.lifecycle.ViewModel
import com.coldfier.feature_search_country.ui.mvi.SearchUiEvent
import com.coldfier.feature_search_country.ui.mvi.processors.SearchStore
import javax.inject.Inject

internal class SearchCountryViewModel @Inject constructor(
    private val searchStore: SearchStore
) : ViewModel() {

    val searchStateFlow = searchStore.stateFlow
    val searchSideEffect = searchStore.sideEffectFlow

    fun sendUiEvent(event: SearchUiEvent) {
        searchStore.consumeEvent(event)
    }

    override fun onCleared() {
        super.onCleared()
        searchStore.onCleared()
    }
}