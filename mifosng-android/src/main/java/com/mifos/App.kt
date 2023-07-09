/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.preference.PreferenceManager
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.evernote.android.job.JobManager
import com.facebook.stetho.Stetho
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.MaterialModule
import com.mifos.mifosxdroid.injection.component.ApplicationComponent
import com.mifos.mifosxdroid.injection.component.DaggerApplicationComponent
import com.mifos.mifosxdroid.injection.module.ApplicationModule
import com.mifos.mifosxdroid.offlinejobs.OfflineJobCreator
import com.mifos.mobile.passcode.utils.ForegroundChecker
import com.mifos.utils.LanguageHelper.onAttach
import com.mifos.utils.ThemeHelper
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import io.fabric.sdk.android.Fabric

/**
 * Created by ishankhanna on 13/03/15.
 */
class App : MultiDexApplication() {
    var mApplicationComponent: ApplicationComponent? = null
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = sharedPreferences.getString("dark_mode", ThemeHelper.DEFAULT_MODE)
        if (themePref != null) {
            ThemeHelper.applyTheme(themePref)
        }
        instance = this
        Fabric.with(this, Crashlytics())
        Iconify.with(MaterialModule())
        JobManager.create(this).addJobCreator(OfflineJobCreator())
        //Initializing the DBFlow and SQL Cipher Encryption
        FlowManager.init(FlowConfig.Builder(this).build())
        Stetho.initializeWithDefaults(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val policy = VmPolicy.Builder()
                .detectFileUriExposure()
                .build()
            StrictMode.setVmPolicy(policy)
        }
        ForegroundChecker.init(this)
    }

    // Needed to replace the component with a test specific one
    var component: ApplicationComponent?
        get() {
            if (mApplicationComponent == null) {
                mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(this))
                    .build()
            }
            return mApplicationComponent
        }
        set(applicationComponent) {
            mApplicationComponent = applicationComponent
        }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(onAttach(base))
    }

    companion object {
        @JvmField
        val typefaceManager: MutableMap<Int, Typeface?> = HashMap()
        @JvmStatic
        var instance: App? =null

        @JvmStatic
        val context: App?
            get() = instance

        @JvmStatic
        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
}