/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.application

import android.content.Context
import android.graphics.Typeface
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.preference.PreferenceManager
import androidx.multidex.MultiDexApplication
import com.evernote.android.job.JobManager
import com.facebook.stetho.Stetho
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.MaterialModule
import com.mifos.mifosxdroid.offlinejobs.OfflineJobCreator
import com.mifos.mobile.passcode.utils.ForegroundChecker
import com.mifos.utils.LanguageHelper.onAttach
import com.mifos.utils.ThemeHelper
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by ishankhanna on 13/03/15.
 */
@HiltAndroidApp
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = sharedPreferences.getString("dark_mode", ThemeHelper.DEFAULT_MODE)
        if (themePref != null) {
            ThemeHelper.applyTheme(themePref)
        }
        instance = this
        Iconify.with(MaterialModule())
        JobManager.create(this).addJobCreator(OfflineJobCreator())
        //Initializing the DBFlow and SQL Cipher Encryption
        FlowManager.init(FlowConfig.Builder(this).build())
        Stetho.initializeWithDefaults(this)
        val policy = VmPolicy.Builder()
            .detectFileUriExposure()
            .build()
        StrictMode.setVmPolicy(policy)
        ForegroundChecker.init(this)
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(onAttach(base))
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