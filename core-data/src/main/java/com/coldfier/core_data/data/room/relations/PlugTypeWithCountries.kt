package com.coldfier.core_data.data.room.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.coldfier.core_data.data.room.models.RoomCountry
import com.coldfier.core_data.data.room.models.RoomPlugType

internal data class PlugTypeWithCountries(
    @Embedded val plugType: RoomPlugType,
    @Relation(
        parentColumn = "plugType",
        entityColumn = "name",
        associateBy = Junction(CountryPlugTypeCrossRef::class)
    )
    val countries: List<RoomCountry>
)
