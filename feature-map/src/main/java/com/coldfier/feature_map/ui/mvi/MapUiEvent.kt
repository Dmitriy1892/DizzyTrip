package com.coldfier.feature_map.ui.mvi

import com.coldfier.core_data.repository.models.CountryShort

internal sealed interface MapUiEvent {
    object PermissionsGranted : MapUiEvent
    class PermissionsDenied(val deniedPermissions: Set<String>) : MapUiEvent
    class CountryChosen(val country: CountryShort) : MapUiEvent
}