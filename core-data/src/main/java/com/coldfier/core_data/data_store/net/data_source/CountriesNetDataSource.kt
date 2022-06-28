package com.coldfier.core_data.data_store.net.data_source

import android.net.Uri
import com.coldfier.core_data.data_store.net.api.CountriesApi
import com.coldfier.core_data.data_store.net.models.NetCountryExpanded
import com.coldfier.core_data.data_store.net.models.NetCountryShort
import javax.inject.Inject

internal class CountriesNetDataSource @Inject constructor(
    private val countriesApi: CountriesApi
) {

    suspend fun getAllCountries(): List<NetCountryShort> {
        return countriesApi.getAllCountries()
    }

    suspend fun getCountryByUrl(uri: Uri): NetCountryExpanded {
        return countriesApi.getCountryByUrl(uri)
    }

    suspend fun searchCountry(countryName: String): NetCountryExpanded {
        return countriesApi.searchCountry(countryName)
    }
}