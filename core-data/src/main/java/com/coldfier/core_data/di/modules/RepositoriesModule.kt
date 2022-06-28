package com.coldfier.core_data.di.modules

import com.coldfier.core_data.di.CoreDataScope
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.CountriesRepositoryImpl
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoriesModule {

    @CoreDataScope
    @Binds
    fun bindCountryRepository(countryRepositoryImpl: CountriesRepositoryImpl): CountriesRepository

    @CoreDataScope
    @Binds
    fun bindPixabayImagesRepository(
        pixabayImagesRepositoryImpl: PixabayImagesRepositoryImpl
    ): PixabayImagesRepository
}