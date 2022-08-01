package com.coldfier.feature_map.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.core_mvi.Middleware
import com.coldfier.feature_map.ui.mvi.MapAction
import com.coldfier.feature_map.ui.mvi.MapState
import com.coldfier.feature_map.ui.mvi.MapUiEvent
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class MapMiddleware @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val pixabayImagesRepository: PixabayImagesRepository
) : Middleware<MapState, MapUiEvent,  MapAction>() {

    val countryShortsFlow = countriesRepository.countryShortsFlow

    override fun invoke(state: MapState, event: MapUiEvent): Flow<MapAction>? {
        return when (event) {
            is MapUiEvent.CountryChosen -> {
                flow {
                    try {
                        val country = getCountryByUri(event.country.uri ?: Uri.EMPTY)
                        emit(MapAction.ChosenCountryInfo(country))
                    } catch (e: Throwable) {
                        emit(MapAction.Error(e))
                    }
                }
            }

            else -> null
        }
    }

    private suspend fun getCountryByUri(uri: Uri): Country {
        return countriesRepository.getCountryByUri(uri)
    }

    suspend fun searchImageByCountryName(countryName: String): Uri? {
        return pixabayImagesRepository.searchImageByCountryName(countryName)
    }
}