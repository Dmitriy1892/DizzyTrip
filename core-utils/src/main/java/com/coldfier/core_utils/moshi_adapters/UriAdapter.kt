package com.coldfier.core_utils.moshi_adapters

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class UriAdapter {
    @FromJson
    fun fromJson(string: String?): Uri? {
        return try {
            Uri.parse(string)
        } catch (e: Exception) {
            null
        }
    }

    @ToJson
    fun toJson(uri: Uri): String {
        return uri.toString()
    }
}