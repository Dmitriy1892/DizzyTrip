package com.coldfier.core_data.data_store.net.api

import android.net.Uri
import com.coldfier.core_data.data_store.net.models.NetCountryExpanded
import com.coldfier.core_data.data_store.net.models.NetCountryShort
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

internal interface CountriesApi {
    companion object {
        const val BASE_URL = "https://travelbriefing.org"
    }

    @GET("/countries.json")
    suspend fun getAllCountries(): List<NetCountryShort>

    @GET("/{country_name}?format=json")
    suspend fun searchCountry(@Path("country_name") countryName: String): NetCountryExpanded

    @GET
    suspend fun getCountryByUrl(@Url uri: Uri): NetCountryExpanded
}
