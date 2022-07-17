package com.coldfier.feature_map.di

import android.content.Context
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.feature_map.MapDeps
import com.coldfier.feature_map.di.modules.MapModule
import com.coldfier.feature_map.di.modules.ViewModelsModule
import com.coldfier.feature_map.ui.MapFragment
import dagger.BindsInstance
import dagger.Component

@MapScope
@Component(
    modules = [MapModule::class, ViewModelsModule::class],
    dependencies = [MapDeps::class]
)
internal interface MapComponent : CoreDataDeps {

    override val context: Context

    fun inject(mapFragment: MapFragment)

    @Component.Builder
    interface Builder {

        fun deps(mapDeps: MapDeps): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): MapComponent
    }
}