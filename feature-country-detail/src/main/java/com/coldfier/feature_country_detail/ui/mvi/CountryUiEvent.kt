package com.coldfier.feature_country_detail.ui.mvi

internal sealed interface CountryUiEvent {
    object ChangeIsBookmark : CountryUiEvent
    class DeniedPermissions(val deniedPermissions: Set<String>) : CountryUiEvent
    object GrantedPermissions : CountryUiEvent
}