package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json

internal data class PixabayResponse(
    @Json(name = "hits")
    var hits: List<PixabayHit>? = null
)
