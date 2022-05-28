package com.coldfier.core_data.data.room.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.coldfier.core_data.data.room.models.RoomCountry
import com.coldfier.core_data.data.room.models.RoomNeighborCountry

internal data class CountryWithNeighborCountries(
    @Embedded val roomCountry: RoomCountry,
    @Relation(
        parentColumn = "name",
        entityColumn = "countryId",
        associateBy = Junction(CountryNeighborCountryCrossRef::class)
    )
    val neighborCountries: List<RoomNeighborCountry>
)
