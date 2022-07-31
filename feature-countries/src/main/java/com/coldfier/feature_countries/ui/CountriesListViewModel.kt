package com.coldfier.feature_countries.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.coldfier.feature_countries.ui.mvi.CountriesUiEvent
import com.coldfier.feature_countries.ui.mvi.processors.CountriesStore
import javax.inject.Inject

internal class CountriesListViewModel @Inject constructor(
    private val countriesStore: CountriesStore
): ViewModel() {

    val countriesStateFlow = countriesStore.stateFlow
    val countriesSideEffectFlow = countriesStore.sideEffectFlow

    fun sendUiEvent(event: CountriesUiEvent) = countriesStore.consumeEvent(event)

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return countriesStore.loadImageByCountryName(countryName)
    }
}