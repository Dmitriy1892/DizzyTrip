package com.coldfier.core_data.data_store.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class RoomVaccination(
    @PrimaryKey(autoGenerate = true)
    var vaccinationId: Int? = null,

    var countryName: String? = null,

    var name: String? = null,
    var message: String? = null
)