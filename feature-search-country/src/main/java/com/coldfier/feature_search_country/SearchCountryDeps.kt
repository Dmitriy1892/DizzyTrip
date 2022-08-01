package com.coldfier.feature_search_country

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.Dependencies

interface SearchCountryDeps : Dependencies {
    fun foundCountryClicked(country: Country)
}