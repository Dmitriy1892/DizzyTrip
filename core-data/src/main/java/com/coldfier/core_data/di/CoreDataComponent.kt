package com.coldfier.core_data.di

import com.coldfier.core_data.CoreDataApi
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.core_data.di.modules.DatabaseModule
import com.coldfier.core_data.di.modules.NetworkModule
import com.coldfier.core_data.di.modules.RepositoriesModule
import dagger.Component

@CoreDataScope
@Component(
    modules = [NetworkModule::class, DatabaseModule::class, RepositoriesModule::class],
    dependencies = [CoreDataDeps::class]
)
internal interface CoreDataComponent {

    fun inject(coreDataApi: CoreDataApi)

    @Component.Builder
    interface Builder {

        fun deps(coreDataDeps: CoreDataDeps): Builder

        fun build(): CoreDataComponent
    }
}