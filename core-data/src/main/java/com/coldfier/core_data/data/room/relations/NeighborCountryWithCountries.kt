package com.coldfier.core_data.data.room.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.coldfier.core_data.data.room.models.RoomCountry
import com.coldfier.core_data.data.room.models.RoomNeighborCountry

internal data class NeighborCountryWithCountries(
    @Embedded val roomNeighborCountry: RoomNeighborCountry,
    @Relation(
        parentColumn = "countryId",
        entityColumn = "name",
        associateBy = Junction(CountryNeighborCountryCrossRef::class)
    )
    val countries: List<RoomCountry>
)
