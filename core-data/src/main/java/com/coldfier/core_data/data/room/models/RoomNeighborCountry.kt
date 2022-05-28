package com.coldfier.core_data.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class RoomNeighborCountry(

    @PrimaryKey(autoGenerate = false)
    var countryId: Int? = null,

    var countryName: String? = null
)