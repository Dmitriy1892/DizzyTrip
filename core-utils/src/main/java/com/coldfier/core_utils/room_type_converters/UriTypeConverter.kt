package com.coldfier.core_utils.room_type_converters

import android.net.Uri
import androidx.room.TypeConverter

class UriTypeConverter {
    @TypeConverter
    fun fromUri(uri: Uri): String = uri.toString()

    @TypeConverter
    fun toUri(string: String?): Uri? = try {
        Uri.parse(string)
    } catch (e: Exception) {
        null
    }
}