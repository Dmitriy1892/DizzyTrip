package com.coldfier.feature_country_detail.ui.mvi

internal sealed interface CountryUiEvent {
    class ChangeIsBookmark(val isNeedToAddBookmark: Boolean) : CountryUiEvent
    object CheckCountryIsBookmark : CountryUiEvent
    class BookmarkStatus(val isBookmark: Boolean) : CountryUiEvent
    class DeniedPermissions(val deniedPermissions: Set<String>) : CountryUiEvent
    object GrantedPermissions : CountryUiEvent
}