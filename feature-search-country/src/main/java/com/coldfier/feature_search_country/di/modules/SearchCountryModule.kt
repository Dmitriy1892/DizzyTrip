package com.coldfier.feature_search_country.di.modules

import android.content.Context
import com.coldfier.core_data.CoreDataApi
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.feature_search_country.di.SearchCountryScope
import com.coldfier.feature_search_country.ui.mvi.SearchState
import dagger.Module
import dagger.Provides

@Module
internal class SearchCountryModule {

    @SearchCountryScope
    @Provides
    fun provideCoreDataDeps(context: Context): CoreDataDeps = object : CoreDataDeps {
        override val context: Context = context
    }

    @SearchCountryScope
    @Provides
    fun provideCountriesRepository(coreDataDeps: CoreDataDeps): CountriesRepository =
        CoreDataApi.getInstance(coreDataDeps).countriesRepository

    @Provides
    fun provideInitialSearchState(): SearchState = SearchState()
}