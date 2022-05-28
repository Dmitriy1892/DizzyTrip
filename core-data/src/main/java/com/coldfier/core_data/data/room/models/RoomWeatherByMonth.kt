package com.coldfier.core_data.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldfier.core_data.domain.models.Month

@Entity
internal data class RoomWeatherByMonth(
    @PrimaryKey(autoGenerate = true)
    var weatherId: Int? = null,

    var countryName: String? = null,

    var month: Month? = null,
    var temperatureAverage: Double? = null,
    var pressureAverage: Double? = null
)
