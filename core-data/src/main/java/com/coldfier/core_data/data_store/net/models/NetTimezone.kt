package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json


internal data class NetTimezone (

  @Json(name = "name" ) var name : String? = null

)