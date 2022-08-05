package com.coldfier.core_data.data_store.room.relations

import androidx.room.Entity
import com.coldfier.core_data.repository.models.PlugType

@Entity(primaryKeys = ["name", "plugType"])
internal data class CountryPlugTypeCrossRef(
    var name: String,
    var plugType: PlugType
)