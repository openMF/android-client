package com.mifos.mifosxdroid;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;

import com.mifos.utils.PrefManager;


@RunWith(AndroidJUnit4ClassRunner.class)
public class SplashScreenActivityTest {

    private ActivityScenario activityScenario;

    @Before
    public void setUp() throws Exception {
        activityScenario = ActivityScenario.launch(SplashScreenActivity.class);
    }

    @Test
    public void testIntentToLoginActivity() {
        if (!PrefManager.isAuthenticated()) {
            onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
        }
    }
}