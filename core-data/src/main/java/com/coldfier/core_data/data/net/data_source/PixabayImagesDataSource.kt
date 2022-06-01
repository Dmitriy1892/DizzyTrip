package com.coldfier.core_data.data.net.data_source

import com.coldfier.core_data.data.net.api.PixabayImagesApi
import com.coldfier.core_data.data.net.models.PixabayResponse
import javax.inject.Inject

internal class PixabayImagesDataSource @Inject constructor(
    private val pixabayImagesApi: PixabayImagesApi
) {

    suspend fun searchImagesByCountryName(countryName: String): PixabayResponse {
        return  pixabayImagesApi.searchImagesByCountryName(countryName)
    }
}