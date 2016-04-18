/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.content.Intent;
import android.os.Bundle;

import com.mifos.App;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.utils.PrefManager;


/**
 * This is the First Activity which can be used for initial checks, inits at app Startup
 */
public class SplashScreenActivity extends MifosBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!PrefManager.isAuthenticated()) {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        } else {
            App.apiManager.setupEndpoint(PrefManager.getInstanceUrl());
            startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
        }
        finish();
    }
}
