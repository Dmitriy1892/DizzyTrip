package com.coldfier.core_data.data_store.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldfier.core_data.repository.models.PlugType

@Entity
internal data class RoomPlugType(

    @PrimaryKey(autoGenerate = false)
    var plugType: PlugType
)
