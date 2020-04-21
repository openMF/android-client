package com.mifos.mifosxdroid.login;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.mifos.mifosxdroid.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {

    private static final String TEST_URL_1 = "demo.mifos.org";
    private static final String TEST_URL_2 = "www.google.com";

    private ActivityScenario activityScenario;
    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule =
            new ActivityTestRule<>(LoginActivity.class);
    private Activity loginActivity;
    private EditText etUserName, etPassword, etMifosDomain, etPort, etTenantIdentifier;
    private ImageView ivMifosLogo;
    private TextView tvConstructedInstanceUrl;
    private Button btLogin;

    @Before
    public void setUp() throws Exception {
        activityScenario = ActivityScenario.launch(LoginActivity.class);
        loginActivity = activityTestRule.getActivity();

        ivMifosLogo = loginActivity.findViewById(R.id.mifos_logo);
        etUserName = loginActivity.findViewById(R.id.et_username);
        etPassword = loginActivity.findViewById(R.id.et_password);
        etMifosDomain = loginActivity.findViewById(R.id.et_instanceURL);
        etPort = loginActivity.findViewById(R.id.et_instancePort);
        etTenantIdentifier = loginActivity.findViewById(R.id.et_tenantIdentifier);
        tvConstructedInstanceUrl = loginActivity.findViewById(R.id
                .tv_constructed_instance_url);
        btLogin = loginActivity.findViewById(R.id.bt_login);
    }

    @Test
    public void testIfActivityOpens() {
        onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testUiWidgetsAreNotNull() {
        assertNotNull(ivMifosLogo);
        assertNotNull(etUserName);
        assertNotNull(etPassword);
        assertNotNull(etMifosDomain);
        assertNotNull(etPort);
        assertNotNull(tvConstructedInstanceUrl);
        assertNotNull(etTenantIdentifier);
        assertNotNull(btLogin);
    }

    @Test
    public void testUiWidgetsAreVisible() {
        assertEquals(View.VISIBLE, ivMifosLogo.getVisibility());
        assertEquals(View.VISIBLE, etUserName.getVisibility());
        assertEquals(View.VISIBLE, etPassword.getVisibility());
        assertEquals(View.VISIBLE, btLogin.getVisibility());
        assertEquals(View.VISIBLE, etMifosDomain.getVisibility());
        assertEquals(View.VISIBLE, etPort.getVisibility());
        assertEquals(View.VISIBLE, tvConstructedInstanceUrl.getVisibility());
        assertEquals(View.VISIBLE, etTenantIdentifier.getVisibility());
    }

    @Test
    public void testURLInstance1() {
        assertNotNull(tvConstructedInstanceUrl);
        enterMifosInstanceDomain(TEST_URL_1);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light),
                tvConstructedInstanceUrl.getCurrentTextColor());
    }

    @Test
    public void testURLInstance2() {
        assertNotNull(tvConstructedInstanceUrl);
        enterMifosInstanceDomain(TEST_URL_2);
        assertEquals(loginActivity.getResources().getColor(R.color.green_light),
                tvConstructedInstanceUrl.getCurrentTextColor());
    }

    private void enterMifosInstanceDomain(final String domain) {
        clearMifosDomainTextInputField();
        getInstrumentation().sendStringSync(domain);
        getInstrumentation().waitForIdleSync();
    }

    private void clearMifosDomainTextInputField() {
        loginActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etMifosDomain.setText("");
                etMifosDomain.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}