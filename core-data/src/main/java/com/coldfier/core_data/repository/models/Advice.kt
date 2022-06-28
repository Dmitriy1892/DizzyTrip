package com.coldfier.core_data.repository.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Advice(
    var advise: String? = null,
    var url: Uri? = null
): Parcelable
