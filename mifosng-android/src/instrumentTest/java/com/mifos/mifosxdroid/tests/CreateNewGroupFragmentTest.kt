/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.tests

import android.test.ActivityInstrumentationTestCase2
import android.view.KeyEvent
import android.view.View
import com.mifos.mifosxdroid.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by ahmed fathy on 20/04/16.
 */
class CreateNewGroupFragmentTest : ActivityInstrumentationTestCase2<DashboardActivity?>(
    DashboardActivity::class.java
) {
    private var mActivity: DashboardActivity? = null
    @Throws(Exception::class)
    fun setUp() {
        super.setUp()
        mActivity = getActivity()
        openCreateGroup()
    }

    /**
     * - chooses "Create Group" from options menu
     * - checks the CrateNewGroupFragment is added
     */
    @SmallTest
    @Throws(InterruptedException::class)
    fun testOpenCreateGroup() {
        val createNewGroupFragment: CreateNewGroupFragment = mActivity.getSupportFragmentManager()
            .findFragmentById(R.id.container) as CreateNewGroupFragment
        assertNotNull(createNewGroupFragment)
        assertTrue(createNewGroupFragment.isAdded())
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testViewsVisible() {

        // initially, the group name, office spinner. submission date,
        // external id, active checkbox and submit button are visible
        Espresso.onView(ViewMatchers.withId(R.id.et_group_name))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.sp_group_offices))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.tv_group_submission_date))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.et_group_external_id))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.cb_group_active_status))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // initially, the activation date is invisible but shows after checking the active checkbox
        Espresso.onView(ViewMatchers.withId(R.id.cb_group_active_status))
            .perform(ViewActions.scrollTo(), ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.tv_group_activationDate))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /**
     * checks that the office spinner is loaded with data from the template
     */
    @SmallTest
    @Throws(InterruptedException::class)
    fun testSpinnerPopulated() {
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.sp_group_offices))
            .check(ViewAssertions.matches(hasChildren()))
    }

    /**
     * fills the template and presses submit
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testCreateGroup() {
        Thread.sleep(3000)

        // group name
        Espresso.onView(ViewMatchers.withId(R.id.et_group_name))
            .perform(ViewActions.scrollTo())
            .perform(ViewActions.typeText("myTestGroup"), ViewActions.closeSoftKeyboard())

        // office
        val spinnerOffice: Spinner = mActivity.findViewById(R.id.sp_group_offices) as Spinner
        if (spinnerOffice.getAdapter().getCount() > 2) {
            val selectedOffice = spinnerOffice.getAdapter().getItem(2) as String
            Espresso.onView(ViewMatchers.withId(R.id.sp_group_offices))
                .perform(ViewActions.scrollTo(), ViewActions.click())
            Espresso.onView(ViewMatchers.withText(selectedOffice))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

        // submission date
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = formatter.format(Date())
        Espresso.onView(ViewMatchers.withId(R.id.tv_group_submission_date))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(currentDate)))

        // external id
        Espresso.onView(ViewMatchers.withId(R.id.et_group_external_id))
            .perform(ViewActions.scrollTo())
            .perform(ViewActions.typeText("123"), ViewActions.closeSoftKeyboard())

        // activation date
        Espresso.onView(ViewMatchers.withId(R.id.cb_group_active_status))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.tv_group_activationDate))
            .check(ViewAssertions.matches(ViewMatchers.withText(currentDate)))

        // submit
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit))
            .perform(ViewActions.scrollTo(), ViewActions.click())
    }

    /**
     * chooses "Create Group" from the options menu
     */
    @Throws(InterruptedException::class)
    private fun openCreateGroup() {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        Espresso.onView(ViewMatchers.withText("Create Group"))
            .perform(ViewActions.click())
    }

    companion object {
        /**
         * checks that the spinner is loaded with 1 or more items
         */
        fun hasChildren(): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                public override fun matchesSafely(view: View): Boolean {
                    return (view as Spinner).getAdapter().getCount() > 0
                }

                override fun describeTo(description: Description) {
                    description.appendText("The spinner has no children")
                }
            }
        }
    }
}