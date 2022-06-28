package com.coldfier.core_data.data_store.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class RoomCountry(

    @PrimaryKey(autoGenerate = false)
    var name: String,

    var fullName: String? = null,
    var iso2: String? = null,
    var continent: String? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var zoom: Double? = null,
    var timezone: String? = null,

    var voltage: Int? = null,
    var frequency: Int? = null,

    var callingCode: Int? = null,
    var policeNumber: Int? = null,
    var ambulanceNumber: Int? = null,
    var fireNumber: Int? = null,
    var waterShort: String? = null,
    var waterFull: String? = null,
    var currencyName: String? = null,
    var currencyCode: String? = null,
    var currencySymbol: String? = null,
)