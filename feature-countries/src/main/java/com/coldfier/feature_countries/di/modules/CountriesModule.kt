package com.coldfier.feature_countries.di.modules

import android.content.Context
import com.coldfier.core_data.CoreDataApi
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.feature_countries.di.CountriesScope
import dagger.Module
import dagger.Provides

@Module
internal class CountriesModule {

    @CountriesScope
    @Provides
    fun provideCoreDataDeps(context: Context): CoreDataDeps = object : CoreDataDeps {
        override val context: Context = context
    }

    @CountriesScope
    @Provides
    fun provideCountriesRepository(coreDataDeps: CoreDataDeps): CountriesRepository =
        CoreDataApi.getInstance(coreDataDeps).countriesRepository

    @CountriesScope
    @Provides
    fun providePixabayImagesRepository(coreDataDeps: CoreDataDeps): PixabayImagesRepository =
        CoreDataApi.getInstance(coreDataDeps).pixabayImagesRepository
}