/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.mifos.mifosxdroid.base.BaseActivity;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;


/**
 * This is the First Activity which can be used for initial checks, inits at app Startup
 */
public class SplashScreenActivity extends BaseActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Crashlytics.start(this);

        Constants.applicationContext = getApplicationContext();
        token = MifosApplication.spManager.getAuthToken();

        /**
         * Authentication Token is checked,
         * if NA(Not Available) User will have to login
         * else User Redirected to Dashboard
         */
        if (token.equals("NA")) {
            //if authentication key is not present
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        } else {
            String instanceURL = MifosApplication.spManager.getInstanceUrl();
            String tenantIdentifier = MifosApplication.spManager.getTenantId();

            MifosApplication.api = new API(instanceURL, tenantIdentifier, false);

            //if authentication key is present open dashboard
            startActivity(new Intent(SplashScreenActivity.this, DashboardFragmentActivity.class));
        }
        finish();
    }
}
