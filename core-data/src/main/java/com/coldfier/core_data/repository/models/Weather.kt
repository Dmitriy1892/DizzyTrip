package com.coldfier.core_data.repository.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    var temperatureAverage : Double? = null,
    var pressureAverage : Double? = null
): Parcelable
