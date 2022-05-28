package com.coldfier.core_data.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.coldfier.core_data.data.room.models.RoomCountry
import com.coldfier.core_data.data.room.models.RoomLanguage

internal data class CountryWithLanguages(
    @Embedded val roomCountry: RoomCountry,
    @Relation(
        parentColumn = "name",
        entityColumn = "countryName"
    )
    val vaccination: List<RoomLanguage>
)
