package com.coldfier.core_data.data_store.net.models

import android.net.Uri
import com.squareup.moshi.Json


internal data class NetAdvise (
  @Json(name = "advise" ) var advise : String? = null,
  @Json(name = "url"    ) var url    : Uri? = null
)