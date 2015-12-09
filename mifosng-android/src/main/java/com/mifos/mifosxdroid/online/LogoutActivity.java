/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mifos.mifosxdroid.SplashScreenActivity;
import com.mifos.objects.User;

/**
 * Logout activity.
 */
public class LogoutActivity extends AppCompatActivity {
    public final static String TAG = LogoutActivity.class.getSimpleName();
    public static final String NA = "NA";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logout(this);
    }

    public void logout(Context context) {
        Log.d(TAG, "logout");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.AUTHENTICATION_KEY, NA);
        editor.commit();
        editor.apply();
        startActivity(new Intent(LogoutActivity.this, SplashScreenActivity.class));
    }
}
