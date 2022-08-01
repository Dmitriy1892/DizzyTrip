package com.coldfier.feature_search_country.ui.mvi

import com.coldfier.core_data.repository.models.Country

internal sealed interface SearchSideEffect {
    class OpenSearchedCountry(val country: Country) : SearchSideEffect
}