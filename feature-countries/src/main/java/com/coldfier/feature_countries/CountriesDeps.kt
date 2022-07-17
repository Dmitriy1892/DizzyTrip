package com.coldfier.feature_countries

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.Dependencies

interface CountriesDeps: Dependencies {
    fun navigateToCountryDetailFragment(country: Country)
}