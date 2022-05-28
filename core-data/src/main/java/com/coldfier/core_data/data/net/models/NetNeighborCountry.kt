package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetNeighborCountry (

  @Json(name = "id"   ) var id   : Int? = null,
  @Json(name = "name" ) var name : String? = null

)