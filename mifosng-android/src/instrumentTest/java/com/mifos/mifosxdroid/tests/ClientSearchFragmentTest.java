package com.mifos.mifosxdroid.tests;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Rajan Maurya on 16/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ClientSearchFragmentTest {

    @Rule
    public ActivityTestRule<DashboardActivity> mDashboardActivity =
            new ActivityTestRule<>(DashboardActivity.class);

    @Before
    public void registerIdlingResource() {
        Espresso.registerIdlingResources(
                mDashboardActivity.getActivity().getCountingIdlingResource());
    }

    @Test
    public void testViewsAreOnTheScreen(){
        onView(withId(R.id.tv_search)).check(matches(withText(R.string.client_search)));
        onView(withId(R.id.et_search_by_id)).check(matches(isDisplayed()));
        onView(withId(R.id.bt_searchClient)).check(matches(withText(R.string.search)));
    }

    @Test
    public void testSearchClient() throws Exception{

        // Add Client Name In EditText
        String clientname = "client";
        onView(withId(R.id.et_search_by_id)).perform(typeText(clientname),closeSoftKeyboard());

        //Search from Rest API
        onView(withId(R.id.bt_searchClient)).perform(click());

    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                mDashboardActivity.getActivity().getCountingIdlingResource());
    }
}
