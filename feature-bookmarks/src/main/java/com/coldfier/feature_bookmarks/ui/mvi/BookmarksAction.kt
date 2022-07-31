package com.coldfier.feature_bookmarks.ui.mvi

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort

internal sealed interface BookmarksAction {
    object Loading : BookmarksAction
    class CountriesListLoaded(val countries: List<CountryShort>) : BookmarksAction
    class NavigateToCountry(val country: Country) : BookmarksAction
    class Error(val error: Throwable) : BookmarksAction
}