package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json

internal data class PixabayHit(
    @Json(name = "webformatURL")
    var webformatURL: String? = null
)
