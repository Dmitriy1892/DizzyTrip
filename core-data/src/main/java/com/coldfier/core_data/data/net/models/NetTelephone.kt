package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetTelephone (

  @Json(name = "calling_code" ) var callingCode : Int? = null,
  @Json(name = "police"       ) var police      : Int? = null,
  @Json(name = "ambulance"    ) var ambulance   : Int? = null,
  @Json(name = "fire"         ) var fire        : Int? = null

)