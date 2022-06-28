package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json


internal data class NetNeighborCountry (

  @Json(name = "id"   ) var id   : String? = null,
  @Json(name = "name" ) var name : String? = null

)