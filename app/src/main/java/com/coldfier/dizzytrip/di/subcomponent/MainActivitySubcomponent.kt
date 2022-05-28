package com.coldfier.dizzytrip.di.subcomponent

import androidx.navigation.NavController
import com.coldfier.dizzytrip.di.subcomponent.modules.ViewModelModule
import com.coldfier.dizzytrip.ui.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@MainActivityScope
@Subcomponent(modules = [ViewModelModule::class])
internal interface MainActivitySubcomponent {

    fun inject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun navController(navController: NavController): Builder

        fun build(): MainActivitySubcomponent
    }
}