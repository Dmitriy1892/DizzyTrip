package com.coldfier.feature_search_country.ui.mvi.processors

import com.coldfier.core_mvi.Reducer
import com.coldfier.feature_search_country.ui.mvi.SearchAction
import com.coldfier.feature_search_country.ui.mvi.SearchResult
import com.coldfier.feature_search_country.ui.mvi.SearchState
import javax.inject.Inject

internal class SearchReducer @Inject constructor(
) : Reducer<SearchState, SearchAction> {

    override fun invoke(state: SearchState, action: SearchAction): SearchState {
        return when (action) {
            is SearchAction.ShowSearchLoadingState -> state.copy(
                searchRequest = action.searchRequest,
                searchResult = SearchResult.Loading
            )
            is SearchAction.SetEmptyRequest -> state.copy(
                searchRequest = "",
                searchResult = null
            )
            is SearchAction.SearchLoading -> {
                if (state.searchRequest.isBlank()) state
                else state.copy(searchResult = SearchResult.Loading)
            }
            is SearchAction.SearchResult -> {
                if (state.searchRequest.isBlank()) state
                else state.copy(searchResult = action.country?.let { SearchResult.Complete(it) })
            }
            is SearchAction.SearchError -> {
                if (state.searchRequest.isBlank()) state
                else state.copy(searchResult = SearchResult.Error(action.error.toString()))
            }

            is SearchAction.OpenSearchedCountry -> state
        }
    }
}