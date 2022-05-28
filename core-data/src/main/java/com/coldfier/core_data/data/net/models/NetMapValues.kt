package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetMapValues (

  @Json(name = "lat"  ) var lat  : Double? = null,
  @Json(name = "long" ) var lon : Double? = null,
  @Json(name = "zoom" ) var zoom : Double? = null

)