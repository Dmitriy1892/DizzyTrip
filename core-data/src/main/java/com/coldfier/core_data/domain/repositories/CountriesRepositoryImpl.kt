package com.coldfier.core_data.domain.repositories

import android.net.Uri
import com.coldfier.core_data.data.net.data_source.CountriesNetDataSource
import com.coldfier.core_data.data.room.data_source.CountriesRoomDataSource
import com.coldfier.core_data.domain.convertToCountry
import com.coldfier.core_data.domain.convertToCountryShort
import com.coldfier.core_data.domain.convertToRoomCountryFullModel
import com.coldfier.core_data.domain.convertToRoomCountryShort
import com.coldfier.core_data.domain.models.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CountriesRepositoryImpl @Inject constructor(
    private val countriesRoomDataSource: CountriesRoomDataSource,
    private val countriesNetDataSource: CountriesNetDataSource
): CountriesRepository {

    override val countryShortsFlow = flow {
        val dbFlow = countriesRoomDataSource.countryShortsFlow.map { roomCountryShorts ->
            roomCountryShorts.map { it.convertToCountryShort() }
        }

        emitAll(dbFlow)

        coroutineScope {
            val countries = countriesNetDataSource.getAllCountries()
                .map { it.convertToRoomCountryShort() }

            countriesRoomDataSource.saveRoomCountryShorts(countries)
        }
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