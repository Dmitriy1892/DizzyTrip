package com.coldfier.core_data.data.net.models

import com.coldfier.core_data.domain.models.PlugType
import com.squareup.moshi.Json


internal data class NetElectricity (

  @Json(name = "voltage"   ) var voltage   : Int?           = null,
  @Json(name = "frequency" ) var frequency : Int?           = null,
  @Json(name = "plugs"     ) var plugs     : List<PlugType>?     = null

)