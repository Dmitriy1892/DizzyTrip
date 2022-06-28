package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json


internal data class NetTelephone (

  @Json(name = "calling_code" ) var callingCode : String? = null,
  @Json(name = "police"       ) var police      : String? = null,
  @Json(name = "ambulance"    ) var ambulance   : String? = null,
  @Json(name = "fire"         ) var fire        : String? = null

)