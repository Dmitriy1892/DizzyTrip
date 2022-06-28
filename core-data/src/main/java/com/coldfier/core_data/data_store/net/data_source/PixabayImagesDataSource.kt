package com.coldfier.core_data.data_store.net.data_source

import com.coldfier.core_data.data_store.net.api.PixabayImagesApi
import com.coldfier.core_data.data_store.net.models.PixabayResponse
import javax.inject.Inject

internal class PixabayImagesDataSource @Inject constructor(
    private val pixabayImagesApi: PixabayImagesApi
) {

    suspend fun searchImagesByCountryName(countryName: String): PixabayResponse {
        return  pixabayImagesApi.searchImagesByCountryName(countryName)
    }
}