/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.mifos.services.API;
import com.orm.SugarApp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishankhanna on 13/03/15.
 */
public class MifosApplication extends SugarApp {

    // Contains fonts to re-user
    public static final Map<Integer, Typeface> typefaceManager = new HashMap<>();

    public static API api;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }

    public static API getApi() {
        return api;
    }
}
