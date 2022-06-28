package com.coldfier.core_data.di.modules

import android.content.Context
import androidx.room.Room
import com.coldfier.core_data.data_store.room.CountryDatabase
import com.coldfier.core_data.data_store.room.dao.CountriesDao
import com.coldfier.core_data.di.CoreDataScope
import dagger.Module
import dagger.Provides

@Module
internal class DatabaseModule {

    @CoreDataScope
    @Provides
    fun provideCountryDatabase(context: Context): CountryDatabase {
        return Room.databaseBuilder(context, CountryDatabase::class.java, "country-database").build()
    }

    @CoreDataScope
    @Provides
    fun provideCountryDao(countryDatabase: CountryDatabase): CountriesDao {
        return countryDatabase.countryDao()
    }
}