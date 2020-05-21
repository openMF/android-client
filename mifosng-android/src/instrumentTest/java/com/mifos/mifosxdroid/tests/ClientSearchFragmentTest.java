package com.mifos.mifosxdroid.tests;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ClientSearchFragmentTest {

    private ActivityScenario activityScenario;

    @Before
    public void setUp() throws Exception {
        activityScenario = ActivityScenario.launch(DashboardActivity.class);
    }

    @Test
    public void testViewsAreNotNull() {
        assertNotNull(withId(R.id.tv_search));
        assertNotNull(withId(R.id.et_search));
        assertNotNull(withId(R.id.btn_search));
    }

    @Test
    public void testViewsAreOnTheScreen() {
        onView(withId(R.id.tv_search)).check(matches(withText(R.string.search)));
        onView(withId(R.id.et_search)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_search)).check(matches(withText(R.string.search)));
    }

    @Test
    public void testSearchClient() {
        String clientName = "client";
        onView(withId(R.id.et_search)).perform(typeText(clientName), closeSoftKeyboard());
        onView(withId(R.id.btn_search)).perform(click());
    }
}
