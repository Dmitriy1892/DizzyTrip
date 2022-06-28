package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json


internal data class NetMapValues (

  @Json(name = "lat"  ) var lat  : String? = null,
  @Json(name = "long" ) var lon : String? = null,
  @Json(name = "zoom" ) var zoom : String? = null

)