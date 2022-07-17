package com.coldfier.feature_countries.di

import android.content.Context
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_countries.di.modules.CountriesModule
import com.coldfier.feature_countries.di.modules.ViewModelModule
import com.coldfier.feature_countries.ui.CountriesListFragment
import dagger.BindsInstance
import dagger.Component

@CountriesScope
@Component(
    modules = [CountriesModule::class, ViewModelModule::class],
    dependencies = [CountriesDeps::class]
)
internal interface CountriesComponent: CoreDataDeps {

    override val context: Context

    fun inject(countriesListFragment: CountriesListFragment)

    @Component.Builder
    interface Builder {

        fun deps(countriesDeps: CountriesDeps): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): CountriesComponent
    }
}