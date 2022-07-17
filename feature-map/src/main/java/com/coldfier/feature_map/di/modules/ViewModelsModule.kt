package com.coldfier.feature_map.di.modules

import androidx.lifecycle.ViewModel
import com.coldfier.core_utils.di.ViewModelKey
import com.coldfier.feature_map.di.MapScope
import com.coldfier.feature_map.ui.MapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelsModule {

    @MapScope
    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel
}