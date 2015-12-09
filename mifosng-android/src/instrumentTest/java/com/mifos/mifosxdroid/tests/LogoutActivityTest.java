package com.mifos.mifosxdroid.tests;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.mifos.mifosxdroid.online.LogoutActivity;
import com.mifos.objects.User;

/**
 * LogoutActivity test.
 */
public class LogoutActivityTest extends ActivityInstrumentationTestCase2<LogoutActivity> {

    public LogoutActivityTest() {
        super(LogoutActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Starts the activity.
        getActivity();
    }

    public void testLogout_resetsAuthenticationKey() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        assertEquals(LogoutActivity.NA, sharedPreferences.getString(User.AUTHENTICATION_KEY, ""));
    }

}
