/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.LoginActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.services.API;
import com.mifos.utils.Constants;

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
    public void testURLInstance1(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        /*
            Set URL and check the color of the message, it turns green
            only if the URL matches the pattern specified
         */
        enterMifosInstanceDomain(TEST_URL_1);

        assertEquals(TEST_URL_1, et_mifos_domain.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());
    }

    @SmallTest
    public void testURLInstance2(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        enterMifosInstanceDomain(TEST_URL_2);

        assertEquals(TEST_URL_2, et_mifos_domain.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());
    }


    @SmallTest
    public void testURLInstance3(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        /*
            Set URL and check the color of the message, it turns green
            only if the URL matches the pattern specified
         */
        enterMifosInstanceDomain(TEST_URL_3);

        assertEquals(TEST_URL_3, et_mifos_domain.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());

    }

    @SmallTest
    public void testURLInstance4(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        /*
            Set URL and check the color of the message, it turns green
            only if the URL matches the pattern specified
         */
        enterMifosInstanceDomain(TEST_URL_4);

        assertEquals(TEST_URL_4, et_mifos_domain.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());
    }

    @SmallTest
    public void testURLInstance5(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);

        /*
            Set URL and check the color of the message, it turns green
            only if the URL matches the pattern specified
         */
        enterMifosInstanceDomain(TEST_URL_5);

        assertEquals(TEST_URL_5, et_mifos_domain.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());
    }

    @MediumTest
    public void testSaveLastAccessedInstanceDomainName_savesProvidedString() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstrumentation().getTargetContext());

        saveLastAccessedInstanceDomainName(TEST_URL_1);
        assertEquals(TEST_URL_1, sharedPreferences.getString(Constants.INSTANCE_URL_KEY, "NA"));

        saveLastAccessedInstanceDomainName(TEST_URL_2);
        assertEquals(TEST_URL_2, sharedPreferences.getString(Constants.INSTANCE_URL_KEY, "NA"));
    }

    @MediumTest
    public void testValidateUserInputs_savesValidDomainToSharedProperties() {
        saveLastAccessedInstanceDomainName(TEST_URL_2);
        enterMifosInstanceDomain(TEST_URL_1);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().validateUserInputs();
                } catch (ShortOfLengthException e) {
                    // ignore
                }
            }
        });
        getInstrumentation().waitForIdleSync();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        assertEquals(TEST_URL_1, sharedPreferences.getString(Constants.INSTANCE_URL_KEY, "NA"));
    }

    @MediumTest
    public void testValidateUserInputs_setsAPIinstanceUrl() {
        saveLastAccessedInstanceDomainName(TEST_URL_2);
        enterMifosInstanceDomain(TEST_URL_1);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().validateUserInputs();
                } catch (ShortOfLengthException e) {
                    // ignore
                }
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals(getActivity().constructInstanceUrl(TEST_URL_1), API.getInstanceUrl());
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
                getActivity().saveLastAccessedInstanceDomainName(domain);
            }
        });
        getInstrumentation().waitForIdleSync();
    }

}
