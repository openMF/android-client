/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.tests

import android.test.ActivityInstrumentationTestCase2
import android.view.View
import com.mifos.mifosxdroid.R

/**
 * Created by ahmed fathy on 26/03/16.
 */
class CreateNewClientFragmentTest : ActivityInstrumentationTestCase2<DashboardActivity?>(
    DashboardActivity::class.java
) {
    /* fields */
    private var dashboardActivity: DashboardActivity? = null
    private var createNewClientFragment: CreateNewClientFragment? = null

    /* views */
    private var textViewDateOfBirth: TextView? = null
    private var textViewSubmissionDate: TextView? = null
    private var checkBoxActive: CheckBox? = null
    @Throws(Exception::class)
    fun setUp() {
        super.setUp()

        // open create client tab
        dashboardActivity = getActivity()
        dashboardActivity.runOnUiThread(Runnable { dashboardActivity.openCreateClient() })
        Thread.sleep(5000)

        // reference views
        createNewClientFragment = dashboardActivity
            .getSupportFragmentManager().findFragmentById(R.id.container) as CreateNewClientFragment
        textViewDateOfBirth = dashboardActivity.findViewById(R.id.tv_dateofbirth) as TextView
        textViewSubmissionDate = dashboardActivity.findViewById(R.id.tv_submission_date) as TextView
        checkBoxActive = dashboardActivity.findViewById(R.id.cb_client_active_status) as CheckBox
    }

    @SmallTest
    fun testViewsNotNull() {
        assertNotNull(textViewDateOfBirth)
        assertNotNull(textViewSubmissionDate)
        assertNotNull(checkBoxActive)
    }

    /**
     * - change birth date, check its text view is update
     * - change submission date, check its text view is updated and birth date isn't
     * - change birth date again, checks its text view is update, submission date isn't
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testChangeDates() {

        // change birth date
        var birthDate = "01-01-1990"
        changeBirthDate(birthDate)
        assertEquals(textViewDateOfBirth.getText().toString(), birthDate)

        // check the active checkbox
        dashboardActivity.runOnUiThread(Runnable { checkBoxActive.setChecked(true) })
        Thread.sleep(1000)
        assertEquals(View.VISIBLE, textViewSubmissionDate.getVisibility())

        // change submission date
        val submissionDate = "02-02-2000"
        changeSubmissionDate(submissionDate)
        assertEquals(textViewSubmissionDate.getText().toString(), submissionDate)
        assertEquals(textViewDateOfBirth.getText().toString(), birthDate)

        // change date of birth again
        birthDate = "03-03-1989"
        changeBirthDate(birthDate)
        assertEquals(textViewSubmissionDate.getText().toString(), submissionDate)
        assertEquals(textViewDateOfBirth.getText().toString(), birthDate)
    }

    @Throws(InterruptedException::class)
    private fun changeBirthDate(dateOfBirth: String) {

        // open the date picker
        Espresso.onView(ViewMatchers.withId(R.id.tv_dateofbirth)).perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.tv_dateofbirth)).perform(ViewActions.click())
        Thread.sleep(1000)
        assertTrue(createNewClientFragment.datePickerDateOfBirth.isAdded())

        // update date and close date picker
        dashboardActivity.runOnUiThread(Runnable { createNewClientFragment.onDatePicked(dateOfBirth) })
        createNewClientFragment.datePickerDateOfBirth.dismiss()
        Thread.sleep(1000)
    }

    @Throws(InterruptedException::class)
    private fun changeSubmissionDate(submissionDate: String) {
        // open the date picker
        Espresso.onView(ViewMatchers.withId(R.id.tv_submission_date))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.tv_submission_date)).perform(ViewActions.click())
        Thread.sleep(1000)
        assertTrue(createNewClientFragment.datePickerSubmissionDate.isAdded())

        // update date and close date picker
        dashboardActivity.runOnUiThread(Runnable {
            createNewClientFragment.onDatePicked(
                submissionDate
            )
        })
        createNewClientFragment.datePickerSubmissionDate.dismiss()
        Thread.sleep(1000)
    }
}