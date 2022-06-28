package com.coldfier.core_data.data_store.net.models

import com.squareup.moshi.Json


internal data class NetCountryNames (

  @Json(name = "name"      ) var name      : String? = null,
  @Json(name = "full"      ) var full      : String? = null,
  @Json(name = "iso2"      ) var iso2      : String? = null,
  @Json(name = "iso3"      ) var iso3      : String? = null,
  @Json(name = "continent" ) var continent : String? = null

)