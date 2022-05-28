package com.coldfier.core_data.data.room.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class RoomCountryShort(
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var uri: Uri? = null
)
