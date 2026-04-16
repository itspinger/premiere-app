package com.premiere.android

import android.app.Application
import com.premiere.di.initKoin
import org.koin.android.ext.koin.androidContext

class PremiereApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@PremiereApplication)
        }
    }
}