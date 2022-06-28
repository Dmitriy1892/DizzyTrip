package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json

internal data class NetWeather(
    @Json(name = "tAvg" ) var temperatureAverage : String? = null,
    @Json(name = "pAvg" ) var pressureAverage : String? = null
)
