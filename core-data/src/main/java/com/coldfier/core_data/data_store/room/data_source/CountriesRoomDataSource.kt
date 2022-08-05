package com.coldfier.core_data.data_store.room.data_source

import com.coldfier.core_data.data_store.room.dao.CountriesDao
import com.coldfier.core_data.data_store.room.models.RoomCountryShort
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class CountriesRoomDataSource @Inject constructor(
    private val countriesDao: CountriesDao
) {

    val countryShortsFlow = countriesDao.getCountryShorts()
        .flowOn(Dispatchers.IO)

    val bookmarksFlow = countriesDao.getBookmarks()
        .flowOn(Dispatchers.IO)

    suspend fun updateIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesDao.updateBookmarkStatus(countryName, isBookmark)
    }

    suspend fun countryIsBookmark(countryName: String): Boolean {
        return countriesDao.countryIsBookmark(countryName)
    }

    suspend fun saveRoomCountryFullModel(roomCountryFullModel: RoomCountryFullModel) {
        coroutineScope {
            val country = roomCountryFullModel.convertToRoomCountry()
            val advices = roomCountryFullModel.advices?.toTypedArray() ?: emptyArray()
            val weathers = roomCountryFullModel.weatherByMonth?.toTypedArray() ?: emptyArray()
            val vaccinations = roomCountryFullModel.vaccinations?.toTypedArray() ?: emptyArray()
            val languages = roomCountryFullModel.languages?.toTypedArray() ?: emptyArray()
            val plugTypes = roomCountryFullModel.plugTypes?.toTypedArray() ?: emptyArray()
            val neighborCountries = roomCountryFullModel.neighborCountries?.toTypedArray() ?: emptyArray()

            val listDeferred = mutableListOf<Deferred<Unit>>().apply {
                with(countriesDao) {
                    add(async { insertCountries(country) })
                    add(async { insertAdvices(*advices) })
                    add(async { insertWeathersByMonth(*weathers) })
                    add(async { insertVaccinations(*vaccinations) })
                    add(async { insertLanguages(*languages) })
                    add(async { insertPlugTypes(*plugTypes) })
                    add(async { insertNeighborCountries(*neighborCountries) })
                }
            }
            listDeferred.awaitAll()
        }
    }

    suspend fun saveRoomCountryShorts(roomCountryShorts: List<RoomCountryShort>) {
        countriesDao.insertCountryShorts(*roomCountryShorts.toTypedArray())
    }
}

