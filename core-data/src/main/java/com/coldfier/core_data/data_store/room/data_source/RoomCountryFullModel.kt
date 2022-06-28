package com.coldfier.core_data.data_store.room.data_source

import com.coldfier.core_data.data_store.room.models.*

internal data class RoomCountryFullModel(
    var name: String? = null,
    var fullName: String? = null,
    var iso2: String? = null,
    var continent: String? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var zoom: Double? = null,
    var timezone: String? = null,
    var languages: List<RoomLanguage>? = null,
    var voltage: Int? = null,
    var frequency: Int? = null,
    var plugTypes: List<RoomPlugType>? = null,
    var callingCode: Int? = null,
    var policeNumber: Int? = null,
    var ambulanceNumber: Int? = null,
    var fireNumber: Int? = null,
    var waterShort: String? = null,
    var waterFull: String? = null,
    var vaccinations: List<RoomVaccination>? = null,
    var currencyName: String? = null,
    var currencyCode: String? = null,
    var currencySymbol: String? = null,
    var weatherByMonth: List<RoomWeatherByMonth>? = null,
    var advices: List<RoomAdvice>? = null,
    var neighborCountries: List<RoomNeighborCountry>? = null
)
