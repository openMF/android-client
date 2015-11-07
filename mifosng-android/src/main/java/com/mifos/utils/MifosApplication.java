/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

import android.content.Context;

import com.mifos.mifosxdroid.manager.SpManager;
import com.mifos.services.API;
import com.orm.SugarApp;

/**
 * Created by ishankhanna on 13/03/15.
 */
public class MifosApplication extends SugarApp {

    public static API api;
    public static final SpManager spManager = new SpManager();
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        spManager.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
