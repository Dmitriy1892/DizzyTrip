package com.coldfier.dizzytrip.di

import android.content.Context
import com.coldfier.dizzytrip.di.modules.SubcomponentsModule
import com.coldfier.dizzytrip.di.subcomponent.MainActivitySubcomponent
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [SubcomponentsModule::class])
internal interface AppComponent {

    fun mainActivityComponent(): MainActivitySubcomponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}