/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos;

import android.content.Context;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.mifos.api.ApiManager;
import com.mifos.mifosxdroid.injection.component.ApplicationComponent;
import com.mifos.mifosxdroid.injection.component.DaggerApplicationComponent;
import com.mifos.mifosxdroid.injection.module.ApplicationModule;
import com.orm.SugarApp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishankhanna on 13/03/15.
 */
public class App extends SugarApp {

    public static final Map<Integer, Typeface> typefaceManager = new HashMap<>();

    public static ApiManager apiManager;

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
        Crashlytics.start(this);

        apiManager = new ApiManager();
        Iconify.with(new MaterialModule());
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
