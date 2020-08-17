/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.mifos.mifosxdroid.injection.component.ApplicationComponent;
import com.mifos.mifosxdroid.injection.component.DaggerApplicationComponent;
import com.mifos.mifosxdroid.injection.module.ApplicationModule;
import com.mifos.mifosxdroid.offlinejobs.OfflineJobCreator;
import com.mifos.mobile.passcode.utils.ForegroundChecker;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ishankhanna on 13/03/15.
 */
public class App extends MultiDexApplication {

    public static final Map<Integer, Typeface> typefaceManager = new HashMap<>();

    private static App instance;

    ApplicationComponent mApplicationComponent;

    public static Context getContext() {
        return instance;
    }

    public static App getInstance() {
        return instance;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Fabric.with(this, new Crashlytics());

        Iconify.with(new MaterialModule());
        JobManager.create(this).addJobCreator(new OfflineJobCreator());
        //Initializing the DBFlow and SQL Cipher Encryption
        FlowManager.init(new FlowConfig.Builder(this).build());
        Stetho.initializeWithDefaults(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .detectFileUriExposure()
                    .build();
            StrictMode.setVmPolicy(policy);
        }
        ForegroundChecker.init(this);
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

}
