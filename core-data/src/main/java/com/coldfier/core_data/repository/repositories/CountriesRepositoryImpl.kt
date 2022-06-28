package com.coldfier.core_data.repository.repositories

import android.net.Uri
import com.coldfier.core_data.data_store.net.data_source.CountriesNetDataSource
import com.coldfier.core_data.data_store.room.data_source.CountriesRoomDataSource
import com.coldfier.core_data.repository.convertToCountry
import com.coldfier.core_data.repository.convertToCountryShort
import com.coldfier.core_data.repository.convertToRoomCountryFullModel
import com.coldfier.core_data.repository.convertToRoomCountryShort
import com.coldfier.core_data.repository.models.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CountriesRepositoryImpl @Inject constructor(
    private val countriesRoomDataSource: CountriesRoomDataSource,
    private val countriesNetDataSource: CountriesNetDataSource
): CountriesRepository {

    override val countryShortsFlow = flow {
        val countries = countriesNetDataSource.getAllCountries()
            .map { it.convertToRoomCountryShort() }
        countriesRoomDataSource.saveRoomCountryShorts(countries)

        val dbFlow = countriesRoomDataSource.countryShortsFlow.map { roomCountryShorts ->
            roomCountryShorts.map { it.convertToCountryShort() }
        }

        emitAll(dbFlow)

    }.flowOn(Dispatchers.IO)

    override suspend fun getCountryByUri(uri: Uri): Country {
        return withContext(Dispatchers.IO) {
            val netCountry = countriesNetDataSource.getCountryByUrl(uri)
            val roomCountry = netCountry.convertToRoomCountryFullModel()
            countriesRoomDataSource.saveRoomCountryFullModel(roomCountry)

            roomCountry.convertToCountry()
        }
    }

    override suspend fun searchCountry(countryName: String): Country {
        return withContext(Dispatchers.IO) {
            val netCountry = countriesNetDataSource.searchCountry(countryName)
            val roomCountry = netCountry.convertToRoomCountryFullModel()
            countriesRoomDataSource.saveRoomCountryFullModel(roomCountry)

            roomCountry.convertToCountry()
        }
    }
}