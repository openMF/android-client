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

import com.mifos.api.BaseUrl;
import com.mifos.mifosxdroid.appintro.AppIntroUser;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.passcode.PassCodeActivity;
import com.mifos.mobile.passcode.utils.PassCodeConstants;
import com.mifos.utils.PrefManager;


/**
 * This is the First Activity which can be used for initial checks, inits at app Startup
 */
public class SplashScreenActivity extends MifosBaseActivity {
    private  Thread mthread;
    private boolean misFirstStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!PrefManager.isAuthenticated()) {
            PrefManager.setInstanceUrl(BaseUrl.PROTOCOL_HTTPS
                    + BaseUrl.API_ENDPOINT + BaseUrl.API_PATH);
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        } else {
            Intent intent = new Intent(SplashScreenActivity.this,
                    PassCodeActivity.class);
            intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true);
            startActivity(intent);
        }
        finish();
        startappintro(this);
    }
    //App intro shred preferences code
    private  void startappintro(final Context context) {
        mthread = new Thread(new Runnable() {
                @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences PREF = PreferenceManager
                        .getDefaultSharedPreferences(context);

                //  Create a new boolean and preference and set it to true
                misFirstStart = PREF.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (misFirstStart) {

                    //  Launch app intro
                    final Intent INTENT = new Intent(SplashScreenActivity.this, AppIntroUser.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(INTENT);
                        }
                    });

                    //  Make a new preferences editor
                    SharedPreferences.Editor E = PREF.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    E.putBoolean("firstStart", false);

                    //  Apply changes
                    E.apply();
                }
            }
        });
        mthread.start();
    }
}
