package com.coldfier.core_data.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coldfier.core_data.data.room.dao.CountriesDao
import com.coldfier.core_data.data.room.models.*
import com.coldfier.core_data.data.room.relations.CountryNeighborCountryCrossRef
import com.coldfier.core_data.data.room.relations.CountryPlugTypeCrossRef
import com.coldfier.core_utils.room_type_converters.UriTypeConverter

@Database(
    entities = [
        RoomAdvice::class, RoomCountry::class, RoomCountryShort::class, RoomLanguage::class,
        RoomNeighborCountry::class, RoomPlugType::class, RoomVaccination::class,
        RoomWeatherByMonth::class, CountryPlugTypeCrossRef::class,
        CountryNeighborCountryCrossRef::class
    ],
    version = 1
)
@TypeConverters(UriTypeConverter::class)
internal abstract class CountryDatabase: RoomDatabase() {
    abstract fun countryDao(): CountriesDao
}