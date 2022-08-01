package com.coldfier.feature_search_country.ui.mvi.processors

import com.coldfier.core_mvi.store.FullStore
import com.coldfier.feature_search_country.ui.mvi.SearchAction
import com.coldfier.feature_search_country.ui.mvi.SearchSideEffect
import com.coldfier.feature_search_country.ui.mvi.SearchState
import com.coldfier.feature_search_country.ui.mvi.SearchUiEvent
import com.coldfier.feature_search_country.use_cases.SearchMiddleware
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class SearchStore @Inject constructor(
    initialState: SearchState,
    override val reducer: SearchReducer,
    override val middleware: SearchMiddleware,
    override val sideEffectProducer: SearchSideEffectProducer
) : FullStore<SearchState, SearchUiEvent, SearchAction, SearchSideEffect>() {

    override val _stateFlow = MutableStateFlow(initialState)

    override fun mapEventToAction(event: SearchUiEvent): SearchAction? {
        return when (event) {
            is SearchUiEvent.OpenSearchedCountry ->
                SearchAction.OpenSearchedCountry(event.country)

            is SearchUiEvent.ShowSearchLoadingState ->
                SearchAction.ShowSearchLoadingState(event.searchRequest)

            is SearchUiEvent.SetEmptySearchRequest -> SearchAction.SetEmptyRequest

            else -> null
        }
    }
}