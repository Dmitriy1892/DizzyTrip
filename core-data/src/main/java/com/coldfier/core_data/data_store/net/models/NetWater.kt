package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json


internal data class NetWater (

  @Json(name = "short" ) var short : String? = null,
  @Json(name = "full"  ) var full  : String? = null

)