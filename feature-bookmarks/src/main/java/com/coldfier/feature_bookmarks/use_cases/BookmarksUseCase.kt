package com.coldfier.feature_bookmarks.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import javax.inject.Inject

class BookmarksUseCase @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val pixabayImagesRepository: PixabayImagesRepository
) {

    val bookmarksFlow = countriesRepository.bookmarksFlow

    suspend fun getCountryByUri(uri: Uri): Country =  countriesRepository.getCountryByUri(uri)

    suspend fun searchImageByCountryName(countryName: String): Uri? {
        return pixabayImagesRepository.searchImageByCountryName(countryName)
    }

    suspend fun changeIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesRepository.updateIsBookmark(countryName, isBookmark)
    }
}