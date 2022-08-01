package com.coldfier.feature_map.ui.mvi.processors

import com.coldfier.core_mvi.Reducer
import com.coldfier.feature_map.ui.mvi.MapAction
import com.coldfier.feature_map.ui.mvi.MapState
import javax.inject.Inject

internal class MapReducer @Inject constructor(
) : Reducer<MapState, MapAction> {

    override fun invoke(state: MapState, action: MapAction): MapState {
        return when (action) {
            is MapAction.ShowProgress -> state.copy(
                isShowLoading = true,
                isShowNoDataLoaded = false,
                isCountrySearchLoading = true
            )

            is MapAction.CountriesListLoaded -> state.copy(
                isShowLoading = false,
                isShowNoDataLoaded = false,
                countryList = action.countriesList,
                isCountrySearchLoading = false
            )

            is MapAction.Error -> state.copy(
                isShowLoading = false,
                isShowNoDataLoaded = state.countryList.isEmpty(),
                isCountrySearchLoading = false
            )

            is MapAction.ChosenCountryInfo -> state.copy(
                isShowLoading = false,
                chosenCountry = action.country,
                isCountrySearchLoading = false
            )

            is MapAction.PermissionsGranted -> state.copy(deniedPermissions = emptySet())

            is MapAction.PermissionsDenied ->
                state.copy(deniedPermissions = action.deniedPermissions)
        }
    }
}