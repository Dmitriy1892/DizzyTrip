package com.coldfier.core_data.data_store.room.dao

import androidx.room.*
import com.coldfier.core_data.data_store.room.models.*
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_data.repository.models.PlugType
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CountriesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountries(vararg roomCountries: RoomCountry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvices(vararg roomAdvices: RoomAdvice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeathersByMonth(vararg roomWeathersByMonth: RoomWeatherByMonth)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccinations(vararg roomVaccinations: RoomVaccination)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(vararg roomLanguages: RoomLanguage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugTypes(vararg roomPlugTypes: RoomPlugType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNeighborCountries(vararg roomNeighborCountries: RoomNeighborCountry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountryShorts(vararg roomCountryShorts: RoomCountryShort)

    @Query("SELECT * FROM roomcountryshort ORDER BY name ASC")
    fun getCountryShorts(): Flow<List<RoomCountryShort>>

    @Query("SELECT * FROM roomcountry ORDER BY name ASC")
    fun getCountries(): Flow<List<RoomCountry>>

    @MapInfo(keyColumn = "name")
    @Query("SELECT * FROM roomcountry " +
            "JOIN roomadvice ON name = roomadvice.countryName")
    fun getCountriesWithAdvicesMap(): Flow<Map<String, List<RoomAdvice>>>

    @MapInfo(keyColumn = "name")
    @Query("SELECT * FROM roomcountry " +
            "JOIN roomweatherbymonth ON name = roomweatherbymonth.countryName")
    fun getCountriesWithWeathers(): Flow<Map<String, List<RoomWeatherByMonth>>>

    @MapInfo(keyColumn = "name")
    @Query("SELECT * FROM roomcountry " +
            "JOIN roomvaccination ON roomcountry.name = roomvaccination.countryName")
    fun getCountriesWithVaccinations(): Flow<Map<String, List<RoomVaccination>>>

    @MapInfo(keyColumn = "name")
    @Query("SELECT * FROM roomcountry " +
            "JOIN roomlanguage ON name = roomlanguage.countryName")
    fun getCountriesWithLanguages(): Flow<Map<String, List<RoomLanguage>>>

    @MapInfo(keyColumn = "name", valueColumn = "plugType")
    @Query("SELECT * FROM countryplugtypecrossref " +
            "JOIN roomplugtype ON countryplugtypecrossref.plugType = roomplugtype.plugType")
    fun getCountriesWithPlugTypes(): Flow<Map<String, List<PlugType>>>

    @MapInfo(keyColumn = "name")
    @Query("SELECT * FROM countryneighborcountrycrossref " +
            "JOIN roomneighborcountry ON countryneighborcountrycrossref.countryId = roomneighborcountry.countryId")
    fun getCountriesWithNeighborCountries(): Flow<Map<String, List<RoomNeighborCountry>>>

    @Query("SELECT * FROM roomcountryshort WHERE isBookmark = 1 ORDER BY name ASC")
    fun getBookmarks(): Flow<List<RoomCountryShort>>

    @Query("UPDATE roomcountryshort SET isBookmark = :isBookmark WHERE name = :countryName")
    suspend fun updateBookmarkStatus(countryName: String, isBookmark: Boolean)

    @Query("SELECT isBookmark FROM roomcountryshort WHERE name = :countryName")
    suspend fun countryIsBookmark(countryName: String): Boolean

//    @Transaction
//    @Query("SELECT * FROM roomcountry WHERE name = :countryName")
//    suspend fun getCountryWithAdvices(countryName: String): Flow<List<CountryWithAdvices>>
//
//    @Transaction
//    @Query("SELECT * FROM roomcountry WHERE name = :countryName")
//    suspend fun getCountryWithWeathers(countryName: String): Flow<List<CountryWithWeathers>>
//
//    @Transaction
//    @Query("SELECT * FROM roomcountry WHERE name = :countryName")
//    suspend fun getCountryWithVaccinations(countryName: String): Flow<List<CountryWithVaccinations>>
//
//    @Transaction
//    @Query("SELECT * FROM roomcountry WHERE name = :countryName")
//    suspend fun getCountryWithLanguages(countryName: String): Flow<List<CountryWithLanguages>>
//
//    @Transaction
//    @Query("SELECT * FROM roomcountry WHERE name = :countryName")
//    suspend fun getCountryWithPlugTypes(countryName: String): Flow<List<CountryWithPlugTypes>>
//
//    @Transaction
//    @Query("SELECT * FROM roomplugtype WHERE plugType = :plugType")
//    suspend fun getPlugTypesWithCountry(plugType: PlugType): Flow<List<CountryWithLanguages>>
//
//    @Transaction
//    @Query("SELECT * FROM roomcountry WHERE name = :countryName")
//    suspend fun getCountryWithNeighborCountries(countryName: String): Flow<List<CountryWithNeighborCountries>>
//
//    @Transaction
//    @Query("SELECT * FROM roomneighborcountry WHERE countryId = :neighborCountryId")
//    suspend fun getNeighborCountryWithCountries(neighborCountryId: Int): Flow<List<NeighborCountryWithCountries>>

}