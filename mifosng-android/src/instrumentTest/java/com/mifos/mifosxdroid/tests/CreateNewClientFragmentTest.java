/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.DashboardActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ahmed fathy on 26/03/16.
 */
public class CreateNewClientFragmentTest extends
        ActivityInstrumentationTestCase2<DashboardActivity> {

    /* fields */
    private DashboardActivity dashboardActivity;
    private CreateNewClientFragment createNewClientFragment;

    /* views */
    private TextView textViewDateOfBirth;
    private TextView textViewSubmissionDate;
    private CheckBox checkBoxActive;

    public CreateNewClientFragmentTest() {
        super(DashboardActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // open create client tab
        dashboardActivity = getActivity();
        dashboardActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dashboardActivity.openCreateClient();
            }
        });
        Thread.sleep(5000);

        // reference views
        createNewClientFragment = (CreateNewClientFragment) dashboardActivity
                .getSupportFragmentManager().findFragmentById(R.id.container);
        textViewDateOfBirth = (TextView) dashboardActivity.findViewById(R.id.tv_dateofbirth);
        textViewSubmissionDate = (TextView) dashboardActivity.findViewById(R.id.tv_submission_date);
        checkBoxActive = (CheckBox) dashboardActivity.findViewById(R.id.cb_client_active_status);
    }

    @SmallTest
    public void testViewsNotNull() {
        assertNotNull(textViewDateOfBirth);
        assertNotNull(textViewSubmissionDate);
        assertNotNull(checkBoxActive);
    }

    /**
     * - change birth date, check its text view is update
     * - change submission date, check its text view is updated and birth date isn't
     * - change birth date again, checks its text view is update, submission date isn't
     */
    @MediumTest
    public void testChangeDates() throws InterruptedException {

        // change birth date
        String birthDate = "01-01-1990";
        changeBirthDate(birthDate);
        assertEquals(textViewDateOfBirth.getText().toString(), birthDate);

        // check the active checkbox
        dashboardActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkBoxActive.setChecked(true);
            }
        });
        Thread.sleep(1000);
        assertEquals(View.VISIBLE, textViewSubmissionDate.getVisibility());

        // change submission date
        final String submissionDate = "02-02-2000";
        changeSubmissionDate(submissionDate);
        assertEquals(textViewSubmissionDate.getText().toString(), submissionDate);
        assertEquals(textViewDateOfBirth.getText().toString(), birthDate);

        // change date of birth again
        birthDate = "03-03-1989";
        changeBirthDate(birthDate);
        assertEquals(textViewSubmissionDate.getText().toString(), submissionDate);
        assertEquals(textViewDateOfBirth.getText().toString(), birthDate);
    }

    private void changeBirthDate(final String dateOfBirth) throws InterruptedException {

        // open the date picker
        onView(withId(R.id.tv_dateofbirth)).perform(scrollTo());
        onView(withId(R.id.tv_dateofbirth)).perform(click());
        Thread.sleep(1000);
        assertTrue(createNewClientFragment.newDatePicker.isAdded());

        // update date and close date picker
        dashboardActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createNewClientFragment.onDatePicked(dateOfBirth);
            }
        });
        createNewClientFragment.newDatePicker.dismiss();
        Thread.sleep(1000);
    }

    private void changeSubmissionDate(final String submissionDate) throws InterruptedException {
        // open the date picker
        onView(withId(R.id.tv_submission_date)).perform(scrollTo());
        onView(withId(R.id.tv_submission_date)).perform(click());
        Thread.sleep(1000);
        assertTrue(createNewClientFragment.mfDatePicker.isAdded());

        // update date and close date picker
        dashboardActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createNewClientFragment.onDatePicked(submissionDate);
            }
        });
        createNewClientFragment.mfDatePicker.dismiss();
        Thread.sleep(1000);
    }


}