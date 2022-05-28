package com.coldfier.core_data.data.net.models

import android.net.Uri
import com.squareup.moshi.Json

internal data class NetCountryShort(
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "url")
    val url: Uri? = null
)
