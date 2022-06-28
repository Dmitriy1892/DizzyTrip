package com.coldfier.core_data.data_store.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class RoomLanguage(
    @PrimaryKey(autoGenerate = true)
    var languageId: Int? = null,

    var countryName: String? = null,

    var language: String? = null,
    var isOfficial: Boolean? = null
)