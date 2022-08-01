package com.coldfier.feature_search_country.use_cases

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_mvi.Middleware
import com.coldfier.feature_search_country.ui.mvi.SearchAction
import com.coldfier.feature_search_country.ui.mvi.SearchState
import com.coldfier.feature_search_country.ui.mvi.SearchUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class SearchMiddleware @Inject constructor(
    private val countriesRepository: CountriesRepository
) : Middleware<SearchState, SearchUiEvent, SearchAction>() {

    override fun invoke(state: SearchState, event: SearchUiEvent): Flow<SearchAction>? {
        return when (event) {
            is SearchUiEvent.SearchCountryByName -> {
                flow {
                    if (event.countryName.isNotBlank()) {
                        emit(SearchAction.SearchLoading)

                        try {
                            val result = searchCountry(event.countryName)
                            emit(SearchAction.SearchResult(result))
                        } catch (e: Throwable) {
                            emit(SearchAction.SearchError(e))
                        }
                    } else {
                        emit(SearchAction.SearchResult(null))
                    }
                }
            }

            else -> null
        }
    }

    private suspend fun searchCountry(countryName: String): Country? {
        return if (countryName.isBlank()) null else countriesRepository.searchCountry(countryName)
    }
}