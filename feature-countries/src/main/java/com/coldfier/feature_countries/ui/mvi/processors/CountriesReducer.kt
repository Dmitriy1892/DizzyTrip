package com.coldfier.feature_countries.ui.mvi.processors

import com.coldfier.core_mvi.Reducer
import com.coldfier.feature_countries.ui.mvi.CountriesAction
import com.coldfier.feature_countries.ui.mvi.CountriesState
import com.coldfier.feature_countries.ui.mvi.SearchResult
import javax.inject.Inject

internal class CountriesReducer @Inject constructor() : Reducer<CountriesState, CountriesAction> {

    override fun invoke(state: CountriesState, action: CountriesAction): CountriesState {
        return when (action) {
            is CountriesAction.SkeletonsLoading -> state.copy(isShowLoadingSkeleton = true)
            is CountriesAction.CountriesListLoaded -> state.copy(
                isShowLoadingSkeleton = false,
                countryShortList = action.countries
            )
            is CountriesAction.Error -> state.copy(
                isShowLoadingSkeleton = false,
                isShowProgress = false
            )

            is CountriesAction.ShowSearchLoadingState -> state.copy(
                searchRequest = action.searchRequest,
                searchResult = SearchResult.Loading
            )
            is CountriesAction.SetEmptyRequest -> state.copy(
                searchRequest = "",
                searchResult = null
            )
            is CountriesAction.SearchLoading -> {
                if (state.searchRequest.isBlank()) state
                else state.copy(searchResult = SearchResult.Loading)
            }
            is CountriesAction.SearchResult -> {
                if (state.searchRequest.isBlank()) state
                else state.copy(searchResult = action.country?.let { SearchResult.Complete(it) })
            }
            is CountriesAction.SearchError -> {
                if (state.searchRequest.isBlank()) state
                else state.copy(searchResult = SearchResult.Error(action.error.toString()))
            }

            is CountriesAction.Loading -> state.copy(isShowProgress = true)
            is CountriesAction.NavigateToDetailScreen -> state.copy(isShowProgress = false)
        }
    }
}