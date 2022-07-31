package com.coldfier.feature_country_detail.ui

import androidx.lifecycle.ViewModel
import com.coldfier.feature_country_detail.ui.mvi.CountryUiEvent
import com.coldfier.feature_country_detail.ui.mvi.processors.CountryStore
import javax.inject.Inject

internal class CountryDetailViewModel @Inject constructor(
    private val countryStore: CountryStore
) : ViewModel() {

    val countryStateFlow = countryStore.stateFlow

    fun sendUiEvent(event: CountryUiEvent) {
        countryStore.consumeEvent(event)
    }

    override fun onCleared() {
        super.onCleared()
        countryStore.onCleared()
    }
}