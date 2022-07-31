package com.coldfier.feature_country_detail.ui.mvi

import android.Manifest
import android.net.Uri
import com.coldfier.core_data.repository.models.Country

internal data class CountryState(
    val country: Country,
    val imageUriList: List<Uri> = listOf(Uri.EMPTY),
    val deniedPermissions: Set<String> = setOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    )
)