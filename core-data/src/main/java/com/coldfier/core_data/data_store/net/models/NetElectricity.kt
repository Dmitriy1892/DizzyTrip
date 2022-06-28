package com.coldfier.core_data.data_store.net.models

import com.coldfier.core_data.repository.models.PlugType
import com.squareup.moshi.Json


internal data class NetElectricity (

  @Json(name = "voltage"   ) var voltage   : String?           = null,
  @Json(name = "frequency" ) var frequency : String?           = null,
  @Json(name = "plugs"     ) var plugs     : List<PlugType>?     = null

)