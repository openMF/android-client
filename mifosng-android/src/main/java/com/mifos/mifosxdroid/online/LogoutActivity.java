/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;

import com.mifos.mifosxdroid.SplashScreenActivity;
import com.mifos.mifosxdroid.base.BaseActivity;

/**
 * Logout activity.
 */
public class LogoutActivity extends BaseActivity {

    public final static String TAG = LogoutActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logout();
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }
}
