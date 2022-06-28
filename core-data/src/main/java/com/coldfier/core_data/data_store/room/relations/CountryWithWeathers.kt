package com.coldfier.core_data.data_store.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.coldfier.core_data.data_store.room.models.RoomCountry
import com.coldfier.core_data.data_store.room.models.RoomWeatherByMonth

internal data class CountryWithWeathers(
    @Embedded val roomCountry: RoomCountry,
    @Relation(
        parentColumn = "name",
        entityColumn = "countryName"
    )
    val weathers: List<RoomWeatherByMonth>
)