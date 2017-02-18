/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.mifos.mifosxdroid.injection.component.ApplicationComponent;
import com.mifos.mifosxdroid.injection.component.DaggerApplicationComponent;
import com.mifos.mifosxdroid.injection.module.ApplicationModule;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ishankhanna on 13/03/15.
 */
public class App extends Application {

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
        //Initializing the DBFlow and SQL Cipher Encryption
        FlowManager.init(new FlowConfig.Builder(this).build());
        Stetho.initializeWithDefaults(this);
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
