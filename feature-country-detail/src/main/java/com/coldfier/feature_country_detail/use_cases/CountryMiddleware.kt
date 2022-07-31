package com.coldfier.feature_country_detail.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.core_mvi.Middleware
import com.coldfier.feature_country_detail.ui.mvi.CountryState
import com.coldfier.feature_country_detail.ui.mvi.CountryUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CountryMiddleware @Inject constructor(
    private val pixabayImagesRepository: PixabayImagesRepository,
    private val countriesRepository: CountriesRepository
) : Middleware<CountryState, CountryUiEvent, CountryUiEvent>() {

    override fun invoke(state: CountryState, event: CountryUiEvent): Flow<CountryUiEvent>? {
        return when (event) {
            is CountryUiEvent.ChangeIsBookmark -> {
                middlewareCoroutineScope.launch {
                    changeIsBookmark(
                        countryName = state.country.name ?: "",
                        isBookmark = state.country.isAddedToBookmark != true
                    )
                }

                null
            }

            else -> null
        }
    }

    suspend fun searchImagesByCountryName(countryName: String): List<Uri> {
        return pixabayImagesRepository.searchImagesByCountryName(countryName)
    }

    private suspend fun changeIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesRepository.updateIsBookmark(countryName, isBookmark)
    }
}