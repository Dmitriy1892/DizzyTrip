package com.coldfier.core_data.di.modules

import com.coldfier.core_data.di.CoreDataScope
import com.coldfier.core_data.domain.repositories.CountriesRepository
import com.coldfier.core_data.domain.repositories.CountriesRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoriesModule {

    @CoreDataScope
    @Binds
    fun bindCountryRepository(countryRepositoryImpl: CountriesRepositoryImpl): CountriesRepository
}