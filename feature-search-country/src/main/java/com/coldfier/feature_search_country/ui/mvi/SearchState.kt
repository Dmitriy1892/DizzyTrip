package com.coldfier.feature_search_country.ui.mvi

import com.coldfier.core_data.repository.models.Country

internal data class SearchState(
    val searchRequest: String = "",
    val searchResult: SearchResult? = null
)

internal sealed interface SearchResult {
    object Loading : SearchResult
    class Complete(val searchResult: Country) : SearchResult
    class Error(val message: String) : SearchResult
}