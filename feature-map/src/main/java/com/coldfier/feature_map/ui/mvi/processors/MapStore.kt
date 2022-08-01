package com.coldfier.feature_map.ui.mvi.processors

import android.net.Uri
import com.coldfier.core_mvi.store.FullStore
import com.coldfier.feature_map.ui.mvi.MapAction
import com.coldfier.feature_map.ui.mvi.MapSideEffect
import com.coldfier.feature_map.ui.mvi.MapState
import com.coldfier.feature_map.ui.mvi.MapUiEvent
import com.coldfier.feature_map.use_cases.MapMiddleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class MapStore @Inject constructor(
    initialState: MapState,
    override val reducer: MapReducer,
    override val middleware: MapMiddleware,
    override val sideEffectProducer: MapSideEffectProducer
) : FullStore<MapState, MapUiEvent, MapAction, MapSideEffect>() {

    override val _stateFlow = MutableStateFlow(initialState)

    init {
        storeCoroutineScope.launch {
            middleware.countryShortsFlow
                .flowOn(Dispatchers.IO)
                .onStart {
                    actionSharedFlow.emit(MapAction.ShowProgress)
                }
                .catch {
                    actionSharedFlow.emit(MapAction.Error(it))
                }
                .onEach {
                    actionSharedFlow.emit(MapAction.CountriesListLoaded(it))
                }
                .collect()
        }
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return middleware.searchImageByCountryName(countryName)
    }

    override fun mapEventToAction(event: MapUiEvent): MapAction? {
        return when (event) {
            is MapUiEvent.FoundCountryClicked -> MapAction.ChosenCountryInfo(event.country)
            is MapUiEvent.PermissionsGranted -> MapAction.PermissionsGranted
            is MapUiEvent.PermissionsDenied -> MapAction.PermissionsDenied(event.deniedPermissions)
            else -> null
        }
    }
}