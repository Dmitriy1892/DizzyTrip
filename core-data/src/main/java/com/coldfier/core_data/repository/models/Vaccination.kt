package com.coldfier.core_data.repository.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vaccination(
    var name: String? = null,
    var message: String? = null
): Parcelable
