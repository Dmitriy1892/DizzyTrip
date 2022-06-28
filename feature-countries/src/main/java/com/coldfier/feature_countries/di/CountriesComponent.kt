package com.coldfier.feature_countries.di

import android.content.Context
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_countries.di.modules.CountriesModule
import com.coldfier.feature_countries.di.modules.ViewModelsModule
import com.coldfier.feature_countries.ui.countries_list.CountriesListFragment
import com.coldfier.feature_countries.ui.country_detail.CountryDetailFragment
import dagger.BindsInstance
import dagger.Component

@CountriesScope
@Component(
    modules = [ViewModelsModule::class, CountriesModule::class],
    dependencies = [CountriesDeps::class]
)
internal interface CountriesComponent: CoreDataDeps {

    override val context: Context

    fun inject(countriesListFragment: CountriesListFragment)
    fun inject(countryDetailFragment: CountryDetailFragment)

    @Component.Builder
    interface Builder {

        fun deps(countriesDeps: CountriesDeps): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): CountriesComponent
    }
}