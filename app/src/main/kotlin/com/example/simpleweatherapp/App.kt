package com.example.simpleweatherapp

import android.app.Application
import com.example.simpleweatherapp.di.appModule
import com.example.simpleweatherapp.utils.PrettyLogcatTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(PrettyLogcatTree())
            Timber.d("@@@ App created")
        }
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
