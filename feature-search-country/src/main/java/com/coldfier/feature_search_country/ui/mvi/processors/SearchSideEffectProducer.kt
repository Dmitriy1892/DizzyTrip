package com.coldfier.feature_search_country.ui.mvi.processors

import com.coldfier.core_mvi.SideEffectProducer
import com.coldfier.feature_search_country.ui.mvi.SearchAction
import com.coldfier.feature_search_country.ui.mvi.SearchSideEffect
import com.coldfier.feature_search_country.ui.mvi.SearchState
import javax.inject.Inject

internal class SearchSideEffectProducer @Inject constructor(
) : SideEffectProducer<SearchState, SearchAction, SearchSideEffect> {

    override fun invoke(state: SearchState, action: SearchAction): SearchSideEffect? {
        return when (action) {
            is SearchAction.OpenSearchedCountry -> {
                SearchSideEffect.OpenSearchedCountry(action.country)
            }

            else -> null
        }
    }
}