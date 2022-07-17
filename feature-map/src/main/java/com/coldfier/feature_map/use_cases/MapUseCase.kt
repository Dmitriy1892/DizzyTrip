package com.coldfier.feature_map.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import javax.inject.Inject

class MapUseCase @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val pixabayImagesRepository: PixabayImagesRepository
) {

    val countryShortsFlow = countriesRepository.countryShortsFlow

    suspend fun getCountryByUri(uri: Uri): Country {
        return countriesRepository.getCountryByUri(uri)
    }

    suspend fun searchImageByCountryName(countryName: String): Uri? {
        return pixabayImagesRepository.searchImageByCountryName(countryName)
    }
}