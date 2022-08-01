package com.coldfier.feature_search_country.di.modules

import androidx.lifecycle.ViewModel
import com.coldfier.core_utils.di.ViewModelKey
import com.coldfier.feature_search_country.di.SearchCountryScope
import com.coldfier.feature_search_country.ui.SearchCountryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelsModule {

    @SearchCountryScope
    @Binds
    @IntoMap
    @ViewModelKey(SearchCountryViewModel::class)
    fun bindSearchCountryViewModel(searchCountryViewModel: SearchCountryViewModel): ViewModel
}