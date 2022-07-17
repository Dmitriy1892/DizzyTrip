package com.coldfier.feature_country_detail.di

import android.content.Context
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.feature_country_detail.CountryDetailDeps
import com.coldfier.feature_country_detail.di.modules.CountryDetailModule
import com.coldfier.feature_country_detail.di.modules.ViewModelModule
import com.coldfier.feature_country_detail.ui.CountryDetailFragment
import dagger.BindsInstance
import dagger.Component

@CountryDetailScope
@Component(
    modules = [CountryDetailModule::class, ViewModelModule::class],
    dependencies = [CountryDetailDeps::class]
)
internal interface CountryDetailComponent : CoreDataDeps {

    override val context: Context

    fun inject(countryDetailFragment: CountryDetailFragment)

    @Component.Builder
    interface Builder {

        fun deps(countryDetailDeps: CountryDetailDeps): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): CountryDetailComponent
    }
}