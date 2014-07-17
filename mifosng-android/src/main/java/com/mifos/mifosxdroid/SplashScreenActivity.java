/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.objects.User;
import com.mifos.utils.Constants;


/**
 * This is the First Activity which can be used for initial checks, inits at app Startup
 */

public class SplashScreenActivity extends ActionBarActivity {

    SharedPreferences sharedPreferences;
    String authenticationToken;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        Crashlytics.start(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        context = SplashScreenActivity.this.getApplicationContext();
        Constants.applicationContext = getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        authenticationToken = sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA");

        /**
         * Authentication Token is checked,
         * if NA(Not Available) User will have to login
         * else User Redirected to Dashboard
         */
        if (authenticationToken.equals("NA")) {
            //if authentication key is not present
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        } else {

            //if authentication key is present open dashboard
            startActivity(new Intent(SplashScreenActivity.this, DashboardFragmentActivity.class));
        }

        finish();

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
            return rootView;
        }
    }

}
