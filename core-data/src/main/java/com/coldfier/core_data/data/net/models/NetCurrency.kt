package com.coldfier.core_data.data.net.models

import com.squareup.moshi.Json


internal data class NetCurrency (

  @Json(name = "name"    ) var name    : String?            = null,
  @Json(name = "code"    ) var code    : String?            = null,
  @Json(name = "symbol"  ) var symbol  : String?            = null,

)