package com.mifos.mifosxdroid.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.ClientSearchFragment;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.PrefManager;

/**
 * Created by Gabriel Esteban on 06/12/14.
 */
@Suppress // TODO: Fix NPE
public class DashboardFragmentActivityTest extends ActivityInstrumentationTestCase2<DashboardFragmentActivity> {

    DashboardFragmentActivity dashboardActivity;
    EditText et_searchById;
    Button bt_searchClient;
    ListView lv_searchResults;
    TextView tv_title_search;
    ClientSearchFragment searchFragment;

    public DashboardFragmentActivityTest() {
        super(DashboardFragmentActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dashboardActivity = getActivity();
        searchFragment = (ClientSearchFragment) getActivity().getSupportFragmentManager().findFragmentByTag(FragmentConstants.FRAG_CLIENT_SEARCH);
        et_searchById = (EditText) dashboardActivity.findViewById(R.id.et_search_by_id);
        bt_searchClient = (Button) dashboardActivity.findViewById(R.id.bt_searchClient);
        lv_searchResults = (ListView) dashboardActivity.findViewById(R.id.lv_searchResults);
        tv_title_search = (TextView) dashboardActivity.findViewById(R.id.tv_title_search_results);
    }

    @SmallTest
    public void testFragmentIsNotNull() {
        assertNotNull(searchFragment);
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
    public void testNullInput(){
        //checking if the text field is empty
        String expected = "";
        String actual = et_searchById.getText().toString();
        assertEquals(expected, actual);

        //clicking the button
        TouchUtils.clickView(this, bt_searchClient);
    }

    @SmallTest
    public void testClientListFragmentShowed(){
        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(dashboardActivity, R.id.mItem_list, 0);

        //if something is wrong, invokeMenuActionSync will take an exception

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @MediumTest
    public void testCenterListActivityStarted(){
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(CentersActivity.class.getName(), null, false);

        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.item_centers, 0);

        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @MediumTest
    public void testOfflineActivityStarted(){
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(OfflineCenterInputActivity.class.getName(), null, false);

        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.item_offline_centers, 0);

        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 2000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    /**
     * Should be tested once the user is logged in.
     */
    @MediumTest
    public void testLogoutActivityStarted(){
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.logout, 0);
        assertEquals("", PrefManager.getToken());
    }
}
