package com.coldfier.core_data.data_store.room.data_source

import com.coldfier.core_data.data_store.room.models.*

internal fun RoomCountry.convertToRoomCountryFullModel() = RoomCountryFullModel(
    name = name,
    fullName = fullName,
    iso2 = iso2,
    continent = continent,
    lat = lat,
    lon = lon,
    zoom = zoom,
    timezone = timezone,
    languages = null,
    voltage = voltage,
    frequency = frequency,
    plugTypes = null,
    callingCode = callingCode,
    policeNumber = policeNumber,
    ambulanceNumber = ambulanceNumber,
    fireNumber = fireNumber,
    waterShort = waterShort,
    waterFull = waterFull,
    vaccinations = null,
    currencyName = currencyName,
    currencyCode = currencyCode,
    currencySymbol = currencySymbol,
    weatherByMonth = null,
    advices = null,
    neighborCountries = null
)

internal fun RoomCountryFullModel.convertToRoomCountry() = RoomCountry(
    name = name ?: "",
    fullName = fullName,
    iso2 = iso2,
    continent = continent,
    lat = lat,
    lon = lon,
    zoom = zoom,
    timezone = timezone,
    voltage = voltage,
    frequency = frequency,
    callingCode = callingCode,
    policeNumber = policeNumber,
    ambulanceNumber = ambulanceNumber,
    fireNumber = fireNumber,
    waterShort = waterShort,
    waterFull = waterFull,
    currencyName = currencyName,
    currencyCode = currencyCode,
    currencySymbol = currencySymbol,
)

internal fun RoomSlots.convertToListRoomCountryFullModel(): List<RoomCountryFullModel> {
    return listCountries.map { roomCountry ->
        roomCountry.convertToRoomCountryFullModel().apply {
            advices = mapAdvices[name]
            weatherByMonth = mapWeathers[name]
            vaccinations = mapVaccinations[name]
            languages = mapLanguages[name]
            plugTypes = mapPlugTypes[name]?.map { RoomPlugType(it) }
            neighborCountries = mapNeighborCountries[name]
        }
    }
}