package com.coldfier.core_data.domain.repositories

import android.net.Uri
import com.coldfier.core_data.domain.models.Country
import com.coldfier.core_data.domain.models.CountryShort
import kotlinx.coroutines.flow.Flow

interface CountriesRepository {

    val countryShortsFlow: Flow<List<CountryShort>>

    suspend fun getCountryByUri(uri: Uri): Country

    suspend fun searchCountry(countryName: String): Country
}