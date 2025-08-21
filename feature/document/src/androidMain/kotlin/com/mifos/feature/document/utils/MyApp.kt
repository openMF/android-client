package com.mifos.feature.document.utils

import android.app.Application

// Global application context (Android only)
lateinit var appContext: Application


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}
