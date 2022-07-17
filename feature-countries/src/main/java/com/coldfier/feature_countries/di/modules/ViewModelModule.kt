package com.coldfier.feature_countries.di.modules

import androidx.lifecycle.ViewModel
import com.coldfier.core_utils.di.ViewModelKey
import com.coldfier.feature_countries.di.CountriesScope
import com.coldfier.feature_countries.ui.CountriesListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {
    @CountriesScope
    @Binds
    @IntoMap
    @ViewModelKey(CountriesListViewModel::class)
    abstract fun bindCountriesListViewModel(countriesListViewModel: CountriesListViewModel): ViewModel
}