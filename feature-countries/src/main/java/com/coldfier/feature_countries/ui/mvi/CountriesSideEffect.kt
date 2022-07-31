package com.coldfier.feature_countries.ui.mvi

import com.coldfier.core_data.repository.models.Country

internal sealed interface CountriesSideEffect {
    class ShowErrorDialog(val error: Throwable) : CountriesSideEffect
    class NavigateToDetailScreen(val country: Country) : CountriesSideEffect
}