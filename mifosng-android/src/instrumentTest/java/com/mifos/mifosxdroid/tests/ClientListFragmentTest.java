package com.mifos.mifosxdroid.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.v4.widget.SwipeRefreshLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;

/**
 * Created by Gabriel Esteban on 12/12/14.
 */
@Suppress // TODO: Fix NPE
public class ClientListFragmentTest extends ActivityInstrumentationTestCase2<DashboardFragmentActivity> {

    DashboardFragmentActivity dashboardActivity;

    ListView lv_clients;
    SwipeRefreshLayout swipeRefreshLayout;

    public ClientListFragmentTest() {
        super(DashboardFragmentActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dashboardActivity = getActivity();
        //starting client list fragment
        dashboardActivity.loadClientList();
        //API wait for charging all clients
        Thread.sleep(8000);
        //instantiating view objects
        lv_clients = (ListView) dashboardActivity.findViewById(R.id.lv_clients);
        swipeRefreshLayout = (SwipeRefreshLayout) dashboardActivity.findViewById(R.id.swipe_container);
    }

    @SmallTest
    public void testViewsAreNotNull() {
        assertNotNull(lv_clients);
        assertNotNull(swipeRefreshLayout);
    }

    @SmallTest
    public void testViewsAreOnTheScreen() {
        final View decorView = dashboardActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, lv_clients);
        ViewAsserts.assertOnScreen(decorView, swipeRefreshLayout);
    }

    @SmallTest
    public void testOpenClientActivity() throws InterruptedException {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(ClientActivity.class.getName(), null, false);

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lv_clients.performItemClick(null, 0, 0);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));

        //waiting for the API
        Thread.sleep(2000);

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }
}
