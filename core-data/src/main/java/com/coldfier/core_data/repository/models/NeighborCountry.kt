package com.coldfier.core_data.repository.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NeighborCountry(
    var countryId: Int? = null,
    var countryName: String? = null
): Parcelable
