package com.coldfier.core_data.domain.repositories

import android.net.Uri

interface PixabayImagesRepository {
    suspend fun searchImagesByCountryName(countryName: String): Uri?
}