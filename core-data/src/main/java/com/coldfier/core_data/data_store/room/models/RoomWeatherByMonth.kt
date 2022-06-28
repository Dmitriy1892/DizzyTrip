package com.coldfier.core_data.data_store.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldfier.core_data.repository.models.Month

@Entity
internal data class RoomWeatherByMonth(
    @PrimaryKey(autoGenerate = true)
    var weatherId: Int? = null,

    var countryName: String? = null,

    var month: Month? = null,
    var temperatureAverage: Double? = null,
    var pressureAverage: Double? = null
)
