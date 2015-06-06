package com.mifos.mifosxdroid.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.utils.Constants;

/**
 * Created by Gabriel Esteban on 12/12/14.
 */
@Suppress // TODO: Fix NPE
public class CenterListFragmentTest extends ActivityInstrumentationTestCase2<CentersActivity> {

    CentersActivity centersActivity;

    ListView lv_centers_list;

    public CenterListFragmentTest() {
        super(CentersActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Constants.applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
        centersActivity = getActivity();

        //API wait for charging all centers
        Thread.sleep(6000);

        //instantiating view objects
        lv_centers_list = (ListView) centersActivity.findViewById(R.id.lv_center_list);
    }

    @SmallTest
    public void testViewsAreNotNull() {
        assertNotNull(lv_centers_list);
    }

    @SmallTest
    public void testViewsAreOnTheScreen() {
        final View decorView = centersActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, lv_centers_list);
    }

    @SmallTest
    public void testOpenClientActivity() throws InterruptedException {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lv_centers_list.performItemClick(null, 0, 0);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        //if something went wrong instantiating the group fragment, performItemClick will throw and exception

        //waiting for the API
        Thread.sleep(2000);

        this.sendKeys(KeyEvent.KEYCODE_BACK);

        //waiting again for the API
        Thread.sleep(6000);
    }
}
