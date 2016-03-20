/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by ishankhanna on 12/08/14.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    String TEST_URL_1 = "demo.mifos.org";
    String TEST_URL_2 = "www.google.com";
    String TEST_URL_3 = "this.is.valid.url";
    String TEST_URL_4 = "yahoo.in";
    String TEST_URL_5 = "10.0.2.2";

    LoginActivity loginActivity;
    EditText et_mifos_domain;
    EditText et_username;
    TextView tv_constructed_instance_url;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();
        et_mifos_domain = (EditText) loginActivity.findViewById(R.id.et_instanceURL);
        et_username = (EditText) loginActivity.findViewById(R.id.et_username);
        tv_constructed_instance_url = (TextView) loginActivity.findViewById(R.id.tv_constructed_instance_url);
    }

    @SmallTest
    public void testEditTextsAreNotNull() {
        assertNotNull(et_mifos_domain);
        assertNotNull(et_username);
    }

    @SmallTest
    public void testAllEditTextsAreVisible() {
        assertEquals(View.VISIBLE, et_mifos_domain.getVisibility());
    }

    @SmallTest
    public void testURLInstance1() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        // Set URL and check the color of the message, it turns green
        // only if the URL matches the pattern specified
        enterMifosInstanceDomain(TEST_URL_1);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light), tv_constructed_instance_url.getCurrentTextColor());
    }

    @SmallTest
    public void testURLInstance2() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);
        enterMifosInstanceDomain(TEST_URL_2);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light), tv_constructed_instance_url.getCurrentTextColor());
    }


    @SmallTest
    public void testURLInstance3() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        enterMifosInstanceDomain(TEST_URL_3);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light), tv_constructed_instance_url.getCurrentTextColor());
    }

    @SmallTest
    public void testURLInstance4() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);
        enterMifosInstanceDomain(TEST_URL_4);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light), tv_constructed_instance_url.getCurrentTextColor());
    }

    @SmallTest
    public void testURLInstance5() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);
        enterMifosInstanceDomain(TEST_URL_5);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light), tv_constructed_instance_url.getCurrentTextColor());
    }

    @MediumTest
    public void testSaveLastAccessedInstanceDomainName_savesProvidedString() {
        PrefManager.setInstanceDomain(TEST_URL_1);
        assertEquals(TEST_URL_1, PrefManager.getInstanceDomain());

        PrefManager.setInstanceDomain(TEST_URL_2);
        assertEquals(TEST_URL_2, PrefManager.getInstanceDomain());
    }

    @MediumTest
    @Suppress // TODO: Fix ComparisonFailure: expected:<[demo.mifos.org]> but was:<[www.google.com]>
    public void testValidateUserInputs_savesValidDomainToSharedProperties() {
        saveLastAccessedInstanceDomainName(TEST_URL_2);
        enterMifosInstanceDomain(TEST_URL_1);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().validateUserInputs();
            }
        });
        getInstrumentation().waitForIdleSync();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        assertEquals(TEST_URL_1, sharedPreferences.getString(Constants.INSTANCE_URL_KEY, ""));
    }

    @MediumTest
    @Suppress // TODO: Fix expected:<https://demo.[mifos.org:80]/mifosng-provider/ap...>
    // but was:<https://demo.[openmf.org]/mifosng-provider/ap...>
    public void testValidateUserInputs_setsAPIinstanceUrl() {
        saveLastAccessedInstanceDomainName(TEST_URL_2);
        enterMifosInstanceDomain(TEST_URL_1);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().validateUserInputs();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @SmallTest
    public void testMoreOptions_displaysOfflineMenuItem() {
        onView(withContentDescription("More options")).perform(click());
        onView(withText(is(startsWith("Offline")))).check(matches(isDisplayed()));
    }

    private void enterMifosInstanceDomain(final String domain) {
        clearMifosDomainTextInputField();
        getInstrumentation().sendStringSync(domain);
        getInstrumentation().waitForIdleSync();
    }

    private void clearMifosDomainTextInputField() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_mifos_domain.setText("");
                et_mifos_domain.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    private void saveLastAccessedInstanceDomainName(final String domain) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                PrefManager.setInstanceDomain(domain);
            }
        });
        getInstrumentation().waitForIdleSync();
    }

}
