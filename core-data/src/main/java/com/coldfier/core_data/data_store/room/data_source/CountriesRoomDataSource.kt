package com.coldfier.core_data.data_store.room.data_source

import com.coldfier.core_data.data_store.room.dao.CountriesDao
import com.coldfier.core_data.data_store.room.models.RoomCountryShort
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

internal class CountriesRoomDataSource @Inject constructor(
    private val countriesDao: CountriesDao
) {
    val countriesFlow = countriesDao.getCountries()
        .zip(countriesDao.getCountriesWithAdvicesMap()) { countries, roomAdvices ->
            RoomSlots(listCountries = countries, mapAdvices = roomAdvices)
        }
        .zip(countriesDao.getCountriesWithWeathers()) { slots, roomWeathers ->
            slots.mapWeathers = roomWeathers
            slots
        }
        .zip(countriesDao.getCountriesWithVaccinations()) { slots, roomVaccinations ->
            slots.mapVaccinations = roomVaccinations
            slots
        }
        .zip(countriesDao.getCountriesWithLanguages()) { slots, roomLanguages ->
            slots.mapLanguages = roomLanguages
            slots
        }
        .zip(countriesDao.getCountriesWithPlugTypes()) { slots, roomPlugTypes ->
            slots.mapPlugTypes = roomPlugTypes
            slots
        }
        .zip(countriesDao.getCountriesWithNeighborCountries()) { slots, roomNeighborCountries ->
            slots.mapNeighborCountries = roomNeighborCountries
            slots.convertToListRoomCountryFullModel()
        }
        .flowOn(Dispatchers.IO)

    val countryShortsFlow = countriesDao.getCountryShorts()
        .flowOn(Dispatchers.IO)

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

