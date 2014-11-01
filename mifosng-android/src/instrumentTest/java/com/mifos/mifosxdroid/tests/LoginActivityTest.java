/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.LoginActivity;
import com.mifos.mifosxdroid.R;

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
    EditText et_instance;
    EditText et_username;
    TextView tv_constructed_instance_url;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();
        et_instance = (EditText) loginActivity.findViewById(R.id.et_instanceURL);
        et_username = (EditText) loginActivity.findViewById(R.id.et_username);
        tv_constructed_instance_url = (TextView) loginActivity.findViewById(R.id.tv_constructed_instance_url);

    }

    @SmallTest
    public void testEditTextsAreNotNull() {

        assertNotNull(et_instance);
        assertNotNull(et_username);

    }

    @SmallTest
    public void testAllEditTextsAreVisible() {

        assertEquals(View.VISIBLE, et_instance.getVisibility());

    }

    @SmallTest
    public void testURLInstance1(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);


        /*
            Set URL and check the color of the message, it turns green
            only if the URL matches the pattern specified
         */
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                et_instance.setText("");
                et_instance.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(TEST_URL_1);
        getInstrumentation().waitForIdleSync();

        assertEquals(TEST_URL_1, et_instance.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());

    }

    @SmallTest
    public void testURLInstance2(){

        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url);


        /*
            Set URL and check the color of the message, it turns green
            only if the URL matches the pattern specified
         */
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                et_instance.setText("");
                et_instance.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(TEST_URL_2);
        getInstrumentation().waitForIdleSync();

        assertEquals(TEST_URL_2, et_instance.getText().toString());
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
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                et_instance.setText("");
                et_instance.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(TEST_URL_3);
        getInstrumentation().waitForIdleSync();

        assertEquals(TEST_URL_3, et_instance.getText().toString());
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
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                et_instance.setText("");
                et_instance.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(TEST_URL_4);
        getInstrumentation().waitForIdleSync();

        assertEquals(TEST_URL_4, et_instance.getText().toString());
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
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                et_instance.setText("");
                et_instance.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(TEST_URL_5);
        getInstrumentation().waitForIdleSync();

        assertEquals(TEST_URL_5, et_instance.getText().toString());
        assertEquals(loginActivity.getResources().getColor(R.color.deposit_green), tv_constructed_instance_url.getCurrentTextColor());

    }


}
