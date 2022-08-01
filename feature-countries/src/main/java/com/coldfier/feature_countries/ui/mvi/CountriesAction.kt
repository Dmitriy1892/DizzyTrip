package com.coldfier.feature_countries.ui.mvi

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort

sealed interface CountriesAction {

    object SkeletonsLoading : CountriesAction

    class CountriesListLoaded(val countries: List<CountryShort>) : CountriesAction

    class Error(val error: Throwable) : CountriesAction

    object Loading : CountriesAction

    class NavigateToDetailScreen(val country: Country) : CountriesAction
}