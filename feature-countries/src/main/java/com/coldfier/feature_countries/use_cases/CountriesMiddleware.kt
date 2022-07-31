package com.coldfier.feature_countries.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.core_mvi.Middleware
import com.coldfier.feature_countries.ui.mvi.CountriesAction
import com.coldfier.feature_countries.ui.mvi.CountriesState
import com.coldfier.feature_countries.ui.mvi.CountriesUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CountriesMiddleware @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val pixabayImagesRepository: PixabayImagesRepository
) : Middleware<CountriesState, CountriesUiEvent, CountriesAction>() {

    val countryShortsFlow = countriesRepository.countryShortsFlow

    val searchCountryFlow: Flow<CountriesAction>? = null

    override fun invoke(state: CountriesState, event: CountriesUiEvent): Flow<CountriesAction>? {
        return when (event) {
            is CountriesUiEvent.SearchCountryByName -> {
                flow {
                    if (event.countryName.isNotBlank()) {
                        emit(CountriesAction.SearchLoading)

                        try {
                            val result = searchCountry(state, event.countryName)
                            emit(CountriesAction.SearchResult(result))
                        } catch (e: Throwable) {
                            emit(CountriesAction.SearchError(e))
                        }
                    } else {
                        emit(CountriesAction.SearchResult(null))
                    }
                }
            }

            is CountriesUiEvent.OpenCountryFullInfo -> {
                flow {
                    emit(CountriesAction.Loading)
                    try {
                        val result = getCountryByUri(event.countryShort.uri!!)
                        emit(CountriesAction.NavigateToDetailScreen(result))
                    } catch (e: Throwable) {
                        emit(CountriesAction.Error(e))
                    }
                }
            }

            is CountriesUiEvent.ChangeIsBookmark -> {
                middlewareCoroutineScope.launch {
                    changeIsBookmark(
                        countryName = event.countryShort.name ?: "",
                        isBookmark = event.countryShort.isAddedToBookmark != true
                    )
                }
                null
            }

            is CountriesUiEvent.OpenUserProfile -> {
                //TODO
                null
            }

            else -> null
        }
    }

    suspend fun searchImageByCountryName(countryName: String): Uri? {
        return pixabayImagesRepository.searchImageByCountryName(countryName)
    }

    private suspend fun searchCountry(state: CountriesState, countryName: String): Country? {
        try {
            if (countryName.isBlank()) return null

            val country = state.countryShortList.find {
                it.name!!.lowercase().contains(countryName.lowercase())
            }

            return if (country == null) {
                searchCountryByName(countryName)
            } else {
                getCountryByUri(country.uri!!)
            }
        } catch (e: Throwable) {
            return null
        }
    }

    private suspend fun searchCountryByName(countryName: String): Country =
        countriesRepository.searchCountry(countryName)

    private suspend fun getCountryByUri(uri: Uri): Country =  countriesRepository.getCountryByUri(uri)

    private suspend fun changeIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesRepository.updateIsBookmark(countryName, isBookmark)
    }
}