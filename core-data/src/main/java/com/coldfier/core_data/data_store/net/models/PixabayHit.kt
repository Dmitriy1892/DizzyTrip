package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json

internal data class PixabayHit(
    @Json(name = "webformatURL")
    var webformatURL: String? = null
)
