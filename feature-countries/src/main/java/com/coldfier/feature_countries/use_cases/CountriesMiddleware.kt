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

    override fun invoke(state: CountriesState, event: CountriesUiEvent): Flow<CountriesAction>? {
        return when (event) {

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

    private suspend fun getCountryByUri(uri: Uri): Country =  countriesRepository.getCountryByUri(uri)

    private suspend fun changeIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesRepository.updateIsBookmark(countryName, isBookmark)
    }
}