package com.coldfier.core_data.di.modules

import com.coldfier.core_data.BuildConfig
import com.coldfier.core_data.data_store.net.AdviceAdapter
import com.coldfier.core_data.data_store.net.api.CountriesApi
import com.coldfier.core_data.data_store.net.api.PixabayImagesApi
import com.coldfier.core_data.di.CoreDataScope
import com.coldfier.core_utils.moshi_adapters.UriAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
internal class NetworkModule {

    private fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .add(UriAdapter())
                    .add(AdviceAdapter())
                    .build()
            )
        )

    private fun provideOkHttpBuilder(): OkHttpClient.Builder = OkHttpClient.Builder().apply {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        if (BuildConfig.DEBUG) addInterceptor(logger)
    }

    @CoreDataScope
    @Provides
    fun provideCountriesApi(): CountriesApi {
        return provideRetrofitBuilder()
            .baseUrl(CountriesApi.BASE_URL)
            .client(provideOkHttpBuilder().build())
            .build()
            .create(CountriesApi::class.java)
    }

    @CoreDataScope
    @Provides
    fun providePixabayImagesApi(): PixabayImagesApi {
        return provideRetrofitBuilder()
            .baseUrl(PixabayImagesApi.BASE_URL)
            .client(provideOkHttpBuilder().build())
            .build()
            .create(PixabayImagesApi::class.java)
    }
}