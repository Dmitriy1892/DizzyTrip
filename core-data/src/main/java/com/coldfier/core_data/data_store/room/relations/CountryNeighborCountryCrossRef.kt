package com.coldfier.core_data.data_store.room.relations

import androidx.room.Entity

@Entity(primaryKeys = ["name", "countryId"])
internal data class CountryNeighborCountryCrossRef(
    var name: String,
    var countryId: Int
)