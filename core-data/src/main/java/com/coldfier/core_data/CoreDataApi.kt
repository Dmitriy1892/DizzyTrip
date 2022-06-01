package com.coldfier.core_data

import com.coldfier.core_data.di.CoreDataComponent
import com.coldfier.core_data.di.DaggerCoreDataComponent
import com.coldfier.core_data.domain.repositories.CountriesRepository
import com.coldfier.core_data.domain.repositories.PixabayImagesRepository
import javax.inject.Inject

class CoreDataApi private constructor(coreDataDeps: CoreDataDeps) {

    init {
        val component: CoreDataComponent = DaggerCoreDataComponent.builder().deps(coreDataDeps).build()
        component.inject(this)
    }

    @Inject
    lateinit var countriesRepository: CountriesRepository

    @Inject
    lateinit var pixabayImagesRepository: PixabayImagesRepository

    companion object {
        @Volatile
        private var INSTANCE: CoreDataApi? = null
        fun getInstance(coreDataDeps: CoreDataDeps): CoreDataApi {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = CoreDataApi(coreDataDeps)
                    INSTANCE!!
                } else {
                    INSTANCE!!
                }
            }
        }
    }
}