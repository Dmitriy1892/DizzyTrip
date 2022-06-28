package com.coldfier.core_data.repository.repositories

import android.net.Uri

interface PixabayImagesRepository {
    suspend fun searchImageByCountryName(countryName: String): Uri?
    suspend fun searchImagesByCountryName(countryName: String): List<Uri>
}