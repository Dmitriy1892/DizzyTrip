package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetLanguage (

  @Json(name = "language" ) var language : String? = null,
  @Json(name = "official" ) var official : String? = null

)