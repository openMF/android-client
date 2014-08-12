/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.EditText;

import com.mifos.mifosxdroid.LoginActivity;
import com.mifos.mifosxdroid.R;

/**
 * Created by ishankhanna on 12/08/14.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity loginActivity;
    EditText et_instance;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();
        et_instance = (EditText) loginActivity.findViewById(R.id.et_instanceURL);
    }

    @SmallTest
    public void testEditTextsAreNotNull() {

        assertNotNull(et_instance);

    }

    @SmallTest
    public void testAllEditTextsAreVisible() {

        assertEquals(View.VISIBLE, et_instance.getVisibility());

    }
}
