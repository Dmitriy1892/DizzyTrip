package com.coldfier.feature_country_detail.di.modules

import android.Manifest
import android.content.Context
import android.net.Uri
import com.coldfier.core_data.CoreDataApi
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.feature_country_detail.CountryDetailDeps
import com.coldfier.feature_country_detail.di.CountryDetailScope
import com.coldfier.feature_country_detail.ui.mvi.CountryState
import dagger.Module
import dagger.Provides

@Module
internal class CountryDetailModule {
    @CountryDetailScope
    @Provides
    fun provideCoreDataDeps(context: Context): CoreDataDeps = object : CoreDataDeps {
        override val context: Context = context
    }

    @CountryDetailScope
    @Provides
    fun provideCountriesRepository(coreDataDeps: CoreDataDeps): CountriesRepository =
        CoreDataApi.getInstance(coreDataDeps).countriesRepository

    @CountryDetailScope
    @Provides
    fun providePixabayImagesRepository(coreDataDeps: CoreDataDeps): PixabayImagesRepository =
        CoreDataApi.getInstance(coreDataDeps).pixabayImagesRepository

    @Provides
    fun provideInitialCountryState(
        countryDetailDeps: CountryDetailDeps
    ): CountryState = CountryState(
        country = countryDetailDeps.country,
        imageUriList = listOf(Uri.EMPTY),
        deniedPermissions = setOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )
}