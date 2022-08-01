package com.coldfier.feature_map.ui.mvi

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort

internal sealed interface MapAction {
    object ShowProgress : MapAction
    class CountriesListLoaded(val countriesList: List<CountryShort>) : MapAction
    class Error(val error: Throwable) : MapAction
    class ChosenCountryInfo(val country: Country) : MapAction
    object PermissionsGranted : MapAction
    class PermissionsDenied(val deniedPermissions: Set<String>) : MapAction
}