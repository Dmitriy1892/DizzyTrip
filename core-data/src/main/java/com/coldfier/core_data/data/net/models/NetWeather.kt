package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json

internal data class NetWeather(
    @Json(name = "tAvg" ) var temperatureAverage : Double? = null,
    @Json(name = "pAvg" ) var pressureAverage : Double? = null
)
