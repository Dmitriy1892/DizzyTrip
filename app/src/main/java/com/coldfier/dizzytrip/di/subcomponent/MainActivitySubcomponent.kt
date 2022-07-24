package com.coldfier.dizzytrip.di.subcomponent

import com.coldfier.dizzytrip.di.subcomponent.modules.ViewModelModule
import com.coldfier.dizzytrip.ui.MainActivity
import dagger.Subcomponent

@MainActivityScope
@Subcomponent(modules = [ViewModelModule::class])
internal interface MainActivitySubcomponent {

    fun inject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        fun build(): MainActivitySubcomponent
    }
}