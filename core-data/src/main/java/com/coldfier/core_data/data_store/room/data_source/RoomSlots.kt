package com.coldfier.core_data.data_store.room.data_source

import com.coldfier.core_data.repository.models.PlugType
import com.coldfier.core_data.data_store.room.models.*

internal data class RoomSlots(
    var listCountries: List<RoomCountry> = listOf(),
    var mapAdvices: Map<String, List<RoomAdvice>> = mapOf(),
    var mapWeathers: Map<String, List<RoomWeatherByMonth>> = mapOf(),
    var mapVaccinations: Map<String, List<RoomVaccination>> = mapOf(),
    var mapLanguages: Map<String, List<RoomLanguage>> = mapOf(),
    var mapPlugTypes: Map<String, List<PlugType>> = mapOf(),
    var mapNeighborCountries: Map<String, List<RoomNeighborCountry>> = mapOf()
)