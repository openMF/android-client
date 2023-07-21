package com.mifos.mifosxdroid.tests

import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.test.espresso.UiController
import com.google.android.material.navigation.NavigationView
import com.mifos.mifosxdroid.R
import org.hamcrest.Matcher

/**
 * Created by Gabriel Esteban on 06/12/14.
 */
@Suppress // TODO: Fix NPE

class DashboardFragmentActivityTest : ActivityInstrumentationTestCase2<DashboardActivity?>(
    DashboardActivity::class.java
) {
    var dashboardActivity: DashboardActivity? = null
    var et_searchById: EditText? = null
    var bt_searchClient: Button? = null
    var lv_searchResults: ListView? = null
    var tv_title_search: TextView? = null
    var searchFragment: SearchFragment? = null
    @Throws(Exception::class)
    protected fun setUp() {
        super.setUp()
        dashboardActivity = getActivity()
        searchFragment = getActivity().getSupportFragmentManager()
            .findFragmentByTag(FragmentConstants.FRAG_CLIENT_SEARCH) as SearchFragment
        et_searchById = dashboardActivity.findViewById(R.id.et_search_by_id) as EditText
        bt_searchClient = dashboardActivity.findViewById(R.id.bt_searchClient)
        lv_searchResults = dashboardActivity.findViewById(R.id.lv_searchResults)
        tv_title_search = dashboardActivity.findViewById(R.id.tv_title_search_results) as TextView
    }

    @SmallTest
    fun testViewsAreNotNull() {
        assertNotNull(et_searchById)
        assertNotNull(bt_searchClient)
        assertNotNull(lv_searchResults)
        assertNotNull(tv_title_search)
    }

    @SmallTest
    fun testViewsAreOnTheScreen() {
        val decorView: View = dashboardActivity.getWindow().getDecorView()
        ViewAsserts.assertOnScreen(decorView, et_searchById)
        ViewAsserts.assertOnScreen(decorView, bt_searchClient)
        ViewAsserts.assertOnScreen(decorView, lv_searchResults)
        ViewAsserts.assertOnScreen(decorView, tv_title_search)
    }

    @SmallTest
    fun testNullInput() {
        //checking if the text field is empty
        val expected = ""
        val actual: String = et_searchById.getText().toString()
        assertEquals(expected, actual)

        //clicking the button
        TouchUtils.clickView(this, bt_searchClient)
    }

    @MediumTest
    @Throws(InterruptedException::class)
    fun testClientListActivityStarted() {
        val monitor: ActivityMonitor =
            getInstrumentation().addMonitor(ClientListActivity::class.java.getName(), null, false)

        // select item from navigation drawer
        Espresso.onView(ViewMatchers.withId(R.id.drawer)).perform(actionOpenDrawer())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.navigation_view))
            .perform(actionSelectNavItem(R.id.item_clients))
        val startedActivity: Activity =
            getInstrumentation().waitForMonitorWithTimeout(monitor, 6000)
        assertNotNull(startedActivity)
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1))
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @MediumTest
    @Throws(InterruptedException::class)
    fun testGroupListActivityStarted() {
        val monitor: ActivityMonitor =
            getInstrumentation().addMonitor(GroupListActivity::class.java.getName(), null, false)

        // select item from navigation drawer
        Espresso.onView(ViewMatchers.withId(R.id.drawer)).perform(actionOpenDrawer())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.navigation_view))
            .perform(actionSelectNavItem(R.id.item_groups))
        val startedActivity: Activity =
            getInstrumentation().waitForMonitorWithTimeout(monitor, 6000)
        assertNotNull(startedActivity)
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1))
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @MediumTest
    @Throws(InterruptedException::class)
    fun testCenterListActivityStarted() {
        val monitor: ActivityMonitor =
            getInstrumentation().addMonitor(CentersActivity::class.java.getName(), null, false)

        // select item from navigation drawer
        Espresso.onView(ViewMatchers.withId(R.id.drawer)).perform(actionOpenDrawer())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.navigation_view))
            .perform(actionSelectNavItem(R.id.item_centers))
        val startedActivity: Activity =
            getInstrumentation().waitForMonitorWithTimeout(monitor, 6000)
        assertNotNull(startedActivity)
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1))
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @MediumTest
    @Throws(InterruptedException::class)
    fun testPathTrackingActivityStarted() {
        val monitor: ActivityMonitor =
            getInstrumentation().addMonitor(PathTrackingActivity::class.java.getName(), null, false)

        // select item from navigation drawer
        Espresso.onView(ViewMatchers.withId(R.id.drawer)).perform(actionOpenDrawer())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.navigation_view))
            .perform(actionSelectNavItem(R.id.item_path_tracker))
        val startedActivity: Activity =
            getInstrumentation().waitForMonitorWithTimeout(monitor, 6000)
        assertNotNull(startedActivity)
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1))
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @MediumTest
    @Throws(InterruptedException::class)
    fun testOfflineActivityStarted() {
        val monitor: ActivityMonitor = getInstrumentation().addMonitor(
            OfflineDashboardFragment::class.java.getName(),
            null,
            false
        )

        // select item from navigation drawer
        Espresso.onView(ViewMatchers.withId(R.id.drawer)).perform(actionOpenDrawer())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.navigation_view))
            .perform(actionSelectNavItem(R.id.item_offline))
        val startedActivity: Activity =
            getInstrumentation().waitForMonitorWithTimeout(monitor, 2000)
        assertNotNull(startedActivity)
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1))
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    /**
     * Should be tested once the user is logged in.
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testLogOut() {
        // select log out from options menu
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.logout, 0)
        assertEquals("", PrefManager.token)
    }

    /**
     * a work around to test opening a nav drawer
     */
    private fun actionOpenDrawer(): ViewAction {
        return object : ViewAction {
            val constraints: Matcher<View>
                get() = ViewMatchers.isAssignableFrom(DrawerLayout::class.java)
            val description: String
                get() = "open drawer"

            override fun perform(uiController: UiController, view: View) {
                (view as DrawerLayout).openDrawer(GravityCompat.START)
            }
        }
    }

    /**
     * a work around to test selecting an item from a navigation view
     */
    private fun actionSelectNavItem(id: Int): ViewAction {
        return object : ViewAction {
            val constraints: Matcher<View>
                get() = ViewMatchers.isAssignableFrom(NavigationView::class.java)
            val description: String
                get() = "select item"

            override fun perform(uiController: UiController, view: View) {
                val item: MenuItem =
                    (view as NavigationView).getMenu().findItem(id).setCheckable(true)
                val listener: NavigationView.OnNavigationItemSelectedListener =
                    getActivity() as NavigationView.OnNavigationItemSelectedListener
                listener.onNavigationItemSelected(item)
            }
        }
    }
}