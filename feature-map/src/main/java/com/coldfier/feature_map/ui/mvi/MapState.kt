package com.coldfier.feature_map.ui.mvi

import android.Manifest
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort

internal data class MapState(
    val isShowLoading: Boolean = false,
    val isShowNoDataLoaded: Boolean = false,
    val countryList: List<CountryShort> = listOf(),
    val chosenCountry: Country? = null,
    val isShowCountrySearchLoading: Boolean = false,
    val deniedPermissions: Set<String> = setOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    ),
    val isNeedToInitMap: Boolean = true,
)