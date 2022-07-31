package com.coldfier.feature_countries.ui.mvi

import android.graphics.drawable.Drawable
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort

internal data class CountriesState(
    val isShowLoadingSkeleton: Boolean = true,
    val isShowProgress: Boolean = false,
    val userAvatar: Drawable? = null,
    val searchRequest: String = "",
    val searchResult: SearchResult? = null,
    val countryShortList: List<CountryShort> = listOf()
)

internal sealed interface SearchResult {
    object Loading : SearchResult
    class Complete(val searchResult: Country) : SearchResult
    class Error(val message: String) : SearchResult
}