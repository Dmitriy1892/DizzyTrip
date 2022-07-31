package com.coldfier.feature_bookmarks.ui.mvi

import com.coldfier.core_data.repository.models.CountryShort

internal sealed interface BookmarksUiEvent {
    class OpenCountryFullInfo(val countryShort: CountryShort) : BookmarksUiEvent
    class ChangeIsBookmark(val countryShort: CountryShort) : BookmarksUiEvent
}