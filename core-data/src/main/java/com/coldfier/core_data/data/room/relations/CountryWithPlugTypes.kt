package com.coldfier.core_data.data.room.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.coldfier.core_data.data.room.models.RoomCountry
import com.coldfier.core_data.data.room.models.RoomPlugType

internal data class CountryWithPlugTypes(
    @Embedded val roomCountry: RoomCountry,
    @Relation(
        parentColumn = "name",
        entityColumn = "plugType",
        associateBy = Junction(CountryPlugTypeCrossRef::class)
    )
    val plugTypes: List<RoomPlugType>
)
