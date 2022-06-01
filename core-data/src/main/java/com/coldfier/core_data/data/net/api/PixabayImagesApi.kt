package com.coldfier.core_data.data.net.api

import com.coldfier.core_data.data.net.models.PixabayResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PixabayImagesApi {
    companion object {
        const val BASE_URL = "https://pixabay.com"
        private const val API_KEY = "27599779-ecc5f1706616729531edb08d9"
    }

    @GET("/api/?key=$API_KEY&image_type=photo&category=places,travel,buildings&order=popular")
    suspend fun searchImagesByCountryName(@Query("q") countryName: String): PixabayResponse
}