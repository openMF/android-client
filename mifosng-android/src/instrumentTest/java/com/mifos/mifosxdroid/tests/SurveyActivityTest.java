/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SurveyActivity;
import com.mifos.mifosxdroid.online.SurveyListFragment;
import com.mifos.mifosxdroid.online.SurveyQuestionViewPager;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ahmed fathy on 11/04/16.
 */
public class SurveyActivityTest extends ActivityInstrumentationTestCase2<SurveyActivity> {


    public SurveyActivityTest() {
        super(SurveyActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    /**
     * choose the first client in the list
     * make sure the survey fragment appears
     */
    @MediumTest
    public void testChooseClient() throws InterruptedException {

        // choose the first client
        Thread.sleep(2000);
        onData(org.hamcrest.core.IsAnything.anything())
                .inAdapterView(withId(R.id.lv_clients))
                .atPosition(0)
                .perform(click());

        // check for the survey fragment
        SurveyListFragment surveyListFragment = (SurveyListFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.container);
        assertNotNull(surveyListFragment);
        assertTrue(surveyListFragment.isAdded());
    }

    /**
     * choose the first client in the list
     * choose the first survey in the list
     * make sure the survey fragment appears
     */
    @MediumTest
    public void testChooseSurvey() throws InterruptedException {

        // choose the first client
        Thread.sleep(2000);
        onData(org.hamcrest.core.IsAnything.anything())
                .inAdapterView(withId(R.id.lv_clients))
                .atPosition(0)
                .perform(click());

        // choose the first survey
        Thread.sleep(2000);
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SurveyQuestionViewPager.class.getName(), null, false);
        onData(org.hamcrest.core.IsAnything.anything())
                .inAdapterView(withId(R.id.lv_surveys_list))
                .atPosition(0)
                .perform(click());

        // check we are in the SurveryQuestionViewPager activity
        Activity startedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 1000);
        assertNotNull(startedActivity);
        assertEquals(true, getInstrumentation().checkMonitorHit(monitor, 1));
    }

}
