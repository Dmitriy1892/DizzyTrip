package com.coldfier.core_data.repository.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Language(
    var language: String? = null,
    var isOfficial: Boolean? = null
): Parcelable
