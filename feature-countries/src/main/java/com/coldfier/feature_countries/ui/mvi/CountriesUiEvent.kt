package com.coldfier.feature_countries.ui.mvi

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort

internal sealed interface CountriesUiEvent {

    object OpenUserProfile : CountriesUiEvent

    class OpenSearchedCountry(val country: Country) : CountriesUiEvent

    class OpenCountryFullInfo(val countryShort: CountryShort) : CountriesUiEvent

    class ChangeIsBookmark(val countryShort: CountryShort) : CountriesUiEvent
}