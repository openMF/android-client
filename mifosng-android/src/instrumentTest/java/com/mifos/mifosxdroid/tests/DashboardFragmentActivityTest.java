package com.mifos.mifosxdroid.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mifos.mifosxdroid.ClientListActivity;
import com.mifos.mifosxdroid.GroupListActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SurveyActivity;
import com.mifos.mifosxdroid.activity.PathTrackingActivity;
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchFragment;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.PrefManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Gabriel Esteban on 06/12/14.
 */
@Suppress // TODO: Fix NPE
public class DashboardFragmentActivityTest extends
        ActivityInstrumentationTestCase2<DashboardActivity> {

    DashboardActivity dashboardActivity;
    EditText et_searchById;
    Button bt_searchClient;
    ListView lv_searchResults;
    TextView tv_title_search;
    ClientSearchFragment searchFragment;

    public DashboardFragmentActivityTest() {
        super(DashboardActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dashboardActivity = getActivity();
        searchFragment = (ClientSearchFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(FragmentConstants.FRAG_CLIENT_SEARCH);
        et_searchById = (EditText) dashboardActivity.findViewById(R.id.et_search_by_id);
        bt_searchClient = (Button) dashboardActivity.findViewById(R.id.bt_searchClient);
        lv_searchResults = (ListView) dashboardActivity.findViewById(R.id.lv_searchResults);
        tv_title_search = (TextView) dashboardActivity.findViewById(R.id.tv_title_search_results);
    }


    @SmallTest
    public void testViewsAreNotNull() {
        assertNotNull(et_searchById);
        assertNotNull(bt_searchClient);
        assertNotNull(lv_searchResults);
        assertNotNull(tv_title_search);
    }

    @SmallTest
    public void testViewsAreOnTheScreen() {
        final View decorView = dashboardActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, et_searchById);
        ViewAsserts.assertOnScreen(decorView, bt_searchClient);
        ViewAsserts.assertOnScreen(decorView, lv_searchResults);
        ViewAsserts.assertOnScreen(decorView, tv_title_search);
    }

    @SmallTest
    public void testNullInput() {
        //checking if the text field is empty
        String expected = "";
        String actual = et_searchById.getText().toString();
        assertEquals(expected, actual);

        //clicking the button
        TouchUtils.clickView(this, bt_searchClient);
    }

    @MediumTest
    public void testClientListActivityStarted() throws InterruptedException {

        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor
                (ClientListActivity.class.getName(), null, false);

        // select item from navigation drawer
        onView(withId(R.id.drawer)).perform(actionOpenDrawer());
        Thread.sleep(500);
        onView(withId(R.id.navigation_view)).perform(actionSelectNavItem(R.id.item_clients));

        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @MediumTest
    public void testGroupListActivityStarted() throws InterruptedException {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor
                (GroupListActivity.class.getName(), null, false);

        // select item from navigation drawer
        onView(withId(R.id.drawer)).perform(actionOpenDrawer());
        Thread.sleep(500);
        onView(withId(R.id.navigation_view)).perform(actionSelectNavItem(R.id.item_groups));


        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @MediumTest
    public void testCenterListActivityStarted() throws InterruptedException {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(CentersActivity
                .class.getName(), null, false);

        // select item from navigation drawer
        onView(withId(R.id.drawer)).perform(actionOpenDrawer());
        Thread.sleep(500);
        onView(withId(R.id.navigation_view)).perform(actionSelectNavItem(R.id.item_centers));


        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @MediumTest
    public void testSurveryActivityStarted() throws InterruptedException {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SurveyActivity
                .class.getName(), null, false);

        // select item from navigation drawer
        onView(withId(R.id.drawer)).perform(actionOpenDrawer());
        Thread.sleep(500);
        onView(withId(R.id.navigation_view)).perform(actionSelectNavItem(R.id.item_survey));


        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @MediumTest
    public void testPathTrackingActivityStarted() throws InterruptedException {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor
                (PathTrackingActivity.class.getName(), null, false);

        // select item from navigation drawer
        onView(withId(R.id.drawer)).perform(actionOpenDrawer());
        Thread.sleep(500);
        onView(withId(R.id.navigation_view)).perform(actionSelectNavItem(R.id.item_path_tracker));


        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }


    @MediumTest
    public void testOfflineActivityStarted() throws InterruptedException {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor
                (OfflineDashboardFragment.class.getName(), null, false);

        // select item from navigation drawer
        onView(withId(R.id.drawer)).perform(actionOpenDrawer());
        Thread.sleep(500);
        onView(withId(R.id.navigation_view)).perform(actionSelectNavItem(R.id.item_offline));

        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 2000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    /**
     * Should be tested once the user is logged in.
     */
    @MediumTest
    public void testLogOut() throws InterruptedException {
        // select log out from options menu
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.logout, 0);

        assertEquals("", PrefManager.getToken());
    }

    /**
     * a work around to test opening a nav drawer
     */
    private ViewAction actionOpenDrawer() {
        return new ViewAction() {

            @Override
            public org.hamcrest.Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).openDrawer(GravityCompat.START);
            }
        };
    }

    /**
     * a work around to test selecting an item from a navigation view
     */
    private ViewAction actionSelectNavItem(final int id) {
        return new ViewAction() {

            @Override
            public org.hamcrest.Matcher<View> getConstraints() {
                return isAssignableFrom(NavigationView.class);
            }

            @Override
            public String getDescription() {
                return "select item";
            }

            @Override
            public void perform(UiController uiController, View view) {

                MenuItem item = ((NavigationView) view).getMenu().findItem(id).setCheckable(true);
                NavigationView.OnNavigationItemSelectedListener listener = (NavigationView
                        .OnNavigationItemSelectedListener) getActivity();
                listener.onNavigationItemSelected(item);
            }
        };
    }

}
