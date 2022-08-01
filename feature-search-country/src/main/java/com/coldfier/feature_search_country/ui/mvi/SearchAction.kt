package com.coldfier.feature_search_country.ui.mvi

import com.coldfier.core_data.repository.models.Country

internal sealed interface SearchAction {
    object SearchLoading : SearchAction
    class SearchResult(val country: Country?) : SearchAction
    class SearchError(val error: Throwable) : SearchAction
    class ShowSearchLoadingState(val searchRequest: String) : SearchAction
    object SetEmptyRequest : SearchAction
    class OpenSearchedCountry(val country: Country) : SearchAction
}