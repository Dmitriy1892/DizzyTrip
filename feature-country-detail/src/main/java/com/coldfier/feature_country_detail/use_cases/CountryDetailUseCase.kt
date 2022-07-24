package com.coldfier.feature_country_detail.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import javax.inject.Inject

class CountryDetailUseCase @Inject constructor(
    private val pixabayImagesRepository: PixabayImagesRepository,
    private val countriesRepository: CountriesRepository
) {
    suspend fun searchImagesByCountryName(countryName: String): List<Uri> {
        return pixabayImagesRepository.searchImagesByCountryName(countryName)
    }

    suspend fun changeIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesRepository.updateIsBookmark(countryName, isBookmark)
    }
}