package com.coldfier.dizzytrip

import android.app.Application
import com.coldfier.dizzytrip.di.AppComponent
import com.coldfier.dizzytrip.di.DaggerAppComponent
import timber.log.Timber

class DizzyTripApplication: Application() {

    internal val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().context(this).build()
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}