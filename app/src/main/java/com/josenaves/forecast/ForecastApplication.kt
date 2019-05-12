package com.josenaves.forecast

import android.app.Application
import com.josenaves.forecast.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ForecastApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ForecastApplication)
            modules(applicationModule)
        }
    }
}
