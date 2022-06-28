package com.coldfier.core_data.repository.repositories

import android.net.Uri

interface PixabayImagesRepository {
    suspend fun searchImagesByCountryName(countryName: String): Uri?
}