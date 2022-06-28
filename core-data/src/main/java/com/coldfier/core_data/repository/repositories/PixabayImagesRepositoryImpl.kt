package com.coldfier.core_data.repository.repositories

import android.net.Uri
import com.coldfier.core_data.data_store.net.data_source.PixabayImagesDataSource
import javax.inject.Inject

internal class PixabayImagesRepositoryImpl @Inject constructor(
    private val pixabayImagesDataSource: PixabayImagesDataSource
) : PixabayImagesRepository {

    override suspend fun searchImagesByCountryName(countryName: String): Uri? {
        val urlString = try {
            pixabayImagesDataSource.searchImagesByCountryName(countryName)
                .hits
                ?.get(0)
                ?.webformatURL
        } catch (e: Exception) {
            null
        }

        return try { Uri.parse(urlString) } catch (e: Exception) { null }
    }
}