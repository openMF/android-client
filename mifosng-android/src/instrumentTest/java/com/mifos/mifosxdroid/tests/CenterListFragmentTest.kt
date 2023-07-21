package com.mifos.mifosxdroid.tests

import android.test.ActivityInstrumentationTestCase2
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ListView

/**
 * Created by Gabriel Esteban on 12/12/14.
 */
@Suppress // TODO: Fix NPE and Rewrite Test with new Documentation

class CenterListFragmentTest : ActivityInstrumentationTestCase2<CentersActivity?>(
    CentersActivity::class.java
) {
    val LOG_TAG: String = javaClass.getSimpleName()
    var centersActivity: CentersActivity? = null
    var lv_centers_list: ListView? = null
    @Throws(Exception::class)
    protected fun setUp() {
        super.setUp()
        centersActivity = getActivity()
        //API wait for charging all centers
        Thread.sleep(6000)
        //instantiating view objects
        //lv_centers_list = (ListView) centersActivity.findViewById(R.id.lv_center_list);
    }

    @SmallTest
    fun testViewsAreNotNull() {
        assertNotNull(lv_centers_list)
    }

    @SmallTest
    fun testViewsAreOnTheScreen() {
        val decorView: View = centersActivity.getWindow().getDecorView()
        ViewAsserts.assertOnScreen(decorView, lv_centers_list)
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testOpenClientActivity() {
        try {
            runTestOnUiThread(Runnable { lv_centers_list!!.performItemClick(null, 0, 0) })
        } catch (throwable: Throwable) {
            Log.d(LOG_TAG, throwable.message!!)
        }

        //if something went wrong instantiating the group fragment, performItemClick will throw
        // and exception

        //waiting for the API
        Thread.sleep(2000)
        this.sendKeys(KeyEvent.KEYCODE_BACK)

        //waiting again for the API
        Thread.sleep(6000)
    }
    /**
     * - open center list, check title is "Centers"
     * - open a center and check title is "Groups"
     * - open a group and check title is "Clients"
     * - press back and check title is "Groups"
     * - press back and check title is "Centers"
     */
    /*@MediumTest
    public void testCorrectTitles() throws InterruptedException {
        assertEquals(getActivity().getTitle().toString(), "Centers");

        // open a center
        Thread.sleep(2000);
        onData(org.hamcrest.core.IsAnything.anything())
                .inAdapterView(withId(R.id.lv_center_list))
                .atPosition(0)
                .perform(click());

        assertEquals(getActivity().getTitle().toString(), "Groups");

        // open a group
        Thread.sleep(2000);
        onData(org.hamcrest.core.IsAnything.anything())
                .inAdapterView(withId(R.id.lv_group_list))
                .atPosition(0)
                .perform(click());
        Thread.sleep(2000);

        assertEquals(getActivity().getTitle().toString(), "Clients");

        // go back to groups
        Thread.sleep(2000);
        sendKeys(KeyEvent.KEYCODE_BACK);

        assertEquals(getActivity().getTitle().toString(), "Groups");

        // go back to cecnters
        Thread.sleep(2000);
        sendKeys(KeyEvent.KEYCODE_BACK);

        assertEquals(getActivity().getTitle().toString(), "Centers");


    }*/
}