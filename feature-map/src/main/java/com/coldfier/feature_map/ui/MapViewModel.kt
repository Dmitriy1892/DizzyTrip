package com.coldfier.feature_map.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.coldfier.feature_map.ui.mvi.MapUiEvent
import com.coldfier.feature_map.ui.mvi.processors.MapStore
import javax.inject.Inject

internal class MapViewModel @Inject constructor(
    private val mapStore: MapStore
) : ViewModel() {

    val mapStateFlow = mapStore.stateFlow
    val mapSideEffectFlow = mapStore.sideEffectFlow

    fun sendUiEvent(event: MapUiEvent) {
        mapStore.consumeEvent(event)
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return mapStore.loadImageForCountry(countryName)
    }
}