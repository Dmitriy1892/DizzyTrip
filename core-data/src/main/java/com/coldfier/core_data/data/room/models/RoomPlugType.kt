package com.coldfier.core_data.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldfier.core_data.domain.models.PlugType

@Entity
internal data class RoomPlugType(

    @PrimaryKey(autoGenerate = false)
    var plugType: PlugType
)
