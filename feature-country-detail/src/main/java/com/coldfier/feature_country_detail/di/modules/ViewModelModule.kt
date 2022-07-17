package com.coldfier.feature_country_detail.di.modules

import androidx.lifecycle.ViewModel
import com.coldfier.core_utils.di.ViewModelKey
import com.coldfier.feature_country_detail.di.CountryDetailScope
import com.coldfier.feature_country_detail.ui.CountryDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @CountryDetailScope
    @Binds
    @IntoMap
    @ViewModelKey(CountryDetailViewModel::class)
    fun bindCountryDetailViewModel(countryDetailViewModel: CountryDetailViewModel): ViewModel
}