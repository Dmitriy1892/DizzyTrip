package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetVaccination (

  @Json(name = "name"    ) var name    : String? = null,
  @Json(name = "message" ) var message : String? = null

)