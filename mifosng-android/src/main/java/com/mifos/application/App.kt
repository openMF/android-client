/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.application

import android.content.Context
import android.graphics.Typeface
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.mifos.cmp.navigation.initKoin
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

/**
 * Created by ishankhanna on 13/03/15.
 */
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        initKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
        }

//        JobManager.create(this).addJobCreator(OfflineJobCreator())
        // Initializing the DBFlow and SQL Cipher Encryption
        FlowManager.init(FlowConfig.Builder(this).build())
        Stetho.initializeWithDefaults(this)
        val policy = VmPolicy.Builder()
            .detectFileUriExposure()
            .build()
        StrictMode.setVmPolicy(policy)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    companion object {
        @JvmField
        val typefaceManager: MutableMap<Int, Typeface?> = HashMap()

        @JvmStatic
        var instance: App? = null

        @JvmStatic
        val context: App?
            get() = instance

        @JvmStatic
        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
}
