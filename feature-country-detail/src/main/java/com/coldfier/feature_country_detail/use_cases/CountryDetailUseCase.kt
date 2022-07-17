package com.coldfier.feature_country_detail.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import javax.inject.Inject

class CountryDetailUseCase @Inject constructor(
    private val pixabayImagesRepository: PixabayImagesRepository
) {
    suspend fun searchImagesByCountryName(countryName: String): List<Uri> {
        return pixabayImagesRepository.searchImagesByCountryName(countryName)
    }
}