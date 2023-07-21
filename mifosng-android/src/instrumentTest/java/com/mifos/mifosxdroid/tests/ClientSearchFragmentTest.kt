package com.mifos.mifosxdroid.tests

import android.test.suitebuilder.annotation.LargeTest
import com.mifos.mifosxdroid.R
import org.junit.After
import org.junit.Rule
import org.junit.Test

/**
 * Created by Rajan Maurya on 16/4/16.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ClientSearchFragmentTest {
    @Rule
    var mDashboardActivity: ActivityTestRule<DashboardActivity> =
        ActivityTestRule<DashboardActivity>(
            DashboardActivity::class.java
        )

    @Before
    fun registerIdlingResource() {
        Espresso.registerIdlingResources(
            mDashboardActivity.getActivity().getCountingIdlingResource()
        )
    }

    @Test
    fun testViewsAreOnTheScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.tv_search)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    R.string.client_search
                )
            )
        )
        Espresso.onView(withId(R.id.et_search_by_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.bt_searchClient)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    R.string.search
                )
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSearchClient() {

        // Add Client Name In EditText
        val clientname = "client"
        Espresso.onView(withId(R.id.et_search_by_id))
            .perform(ViewActions.typeText(clientname), ViewActions.closeSoftKeyboard())

        //Search from Rest API
        Espresso.onView(withId(R.id.bt_searchClient)).perform(ViewActions.click())
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
            mDashboardActivity.getActivity().getCountingIdlingResource()
        )
    }
}