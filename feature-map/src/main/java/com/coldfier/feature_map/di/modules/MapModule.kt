package com.coldfier.feature_map.di.modules

import android.content.Context
import com.coldfier.core_data.CoreDataApi
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.feature_map.di.MapScope
import com.coldfier.feature_map.ui.mvi.MapState
import dagger.Module
import dagger.Provides

@Module
internal class MapModule {

    @MapScope
    @Provides
    fun provideCoreDataDeps(context: Context): CoreDataDeps = object : CoreDataDeps {
        override val context: Context = context
    }

    @MapScope
    @Provides
    fun provideCountriesRepository(coreDataDeps: CoreDataDeps): CountriesRepository =
        CoreDataApi.getInstance(coreDataDeps).countriesRepository

    @MapScope
    @Provides
    fun providePixabayImagesRepository(coreDataDeps: CoreDataDeps): PixabayImagesRepository =
        CoreDataApi.getInstance(coreDataDeps).pixabayImagesRepository

    @Provides
    fun provideInitialMapState(): MapState = MapState()
}