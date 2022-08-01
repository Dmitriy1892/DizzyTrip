package com.coldfier.feature_search_country.ui.mvi

import com.coldfier.core_data.repository.models.Country

internal sealed interface SearchUiEvent {
    class ShowSearchLoadingState(val searchRequest: String) : SearchUiEvent
    object SetEmptySearchRequest : SearchUiEvent
    class SearchCountryByName(val countryName: String) : SearchUiEvent
    class OpenSearchedCountry(val country: Country) : SearchUiEvent
}