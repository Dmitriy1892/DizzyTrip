package com.coldfier.feature_search_country.di

import android.content.Context
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.feature_search_country.SearchCountryDeps
import com.coldfier.feature_search_country.di.modules.SearchCountryModule
import com.coldfier.feature_search_country.di.modules.ViewModelsModule
import com.coldfier.feature_search_country.ui.SearchCountryFragment
import dagger.BindsInstance
import dagger.Component

@SearchCountryScope
@Component(
    modules = [SearchCountryModule::class, ViewModelsModule::class],
    dependencies = [SearchCountryDeps::class]
)
internal interface SearchCountryComponent : CoreDataDeps {

    override val context: Context

    fun inject(searchCountryFragment: SearchCountryFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            deps: SearchCountryDeps
        ): SearchCountryComponent
    }
}