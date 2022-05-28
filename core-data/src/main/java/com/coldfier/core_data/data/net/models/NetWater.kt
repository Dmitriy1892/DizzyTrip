package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetWater (

  @Json(name = "short" ) var short : String? = null,
  @Json(name = "full"  ) var full  : String? = null

)