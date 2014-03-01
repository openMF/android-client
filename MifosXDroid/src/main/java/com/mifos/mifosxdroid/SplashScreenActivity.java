package com.mifos.mifosxdroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.objects.User;


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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        context = SplashScreenActivity.this.getBaseContext();

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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
