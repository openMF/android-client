package com.mifos.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import com.mifos.mifosxdroid.R;

import java.util.Locale;

/**
 * Created by shashankpriyadarshi on 20/06/20.
 */

public class LanguageHelper {

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    public static Context setLocale(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.language_type), defaultLanguage);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language.toLowerCase());
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
