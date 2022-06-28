package com.coldfier.feature_countries.di.modules

import androidx.lifecycle.ViewModel
import com.coldfier.core_utils.di.ViewModelKey
import com.coldfier.feature_countries.ui.countries_list.CountriesListViewModel
import com.coldfier.feature_countries.ui.country_detail.CountryDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(CountriesListViewModel::class)
    fun bindCountriesListViewModel(countriesListViewModel: CountriesListViewModel): ViewModel
}