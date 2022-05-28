package com.coldfier.feature_countries.use_cases

import android.net.Uri
import com.coldfier.core_data.domain.models.Country
import com.coldfier.core_data.domain.repositories.CountriesRepository
import javax.inject.Inject

internal class CountriesListUseCase @Inject constructor(
    private val countriesRepository: CountriesRepository
) {

    val countryShortsFlow = countriesRepository.countryShortsFlow

    suspend fun getCountryByUri(uri: Uri): Country =  countriesRepository.getCountryByUri(uri)

    suspend fun searchCountry(countryName: String): Country =
        countriesRepository.searchCountry(countryName)

}