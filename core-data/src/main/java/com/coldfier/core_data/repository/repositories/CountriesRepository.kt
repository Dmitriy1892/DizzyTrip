package com.coldfier.core_data.repository.repositories

import android.net.Uri
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import kotlinx.coroutines.flow.Flow

interface CountriesRepository {

    val countryShortsFlow: Flow<List<CountryShort>>

    suspend fun getCountryByUri(uri: Uri): Country

    suspend fun searchCountry(countryName: String): Country
}