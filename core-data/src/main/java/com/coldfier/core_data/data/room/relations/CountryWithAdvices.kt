package com.coldfier.core_data.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.coldfier.core_data.data.room.models.RoomAdvice
import com.coldfier.core_data.data.room.models.RoomCountry

internal data class CountryWithAdvices(
    @Embedded val roomCountry: RoomCountry,
    @Relation(
        parentColumn = "name",
        entityColumn = "countryName"
    )
    val advices: List<RoomAdvice>
)
