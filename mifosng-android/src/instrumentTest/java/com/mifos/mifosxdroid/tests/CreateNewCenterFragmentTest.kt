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
 * Created by ahmed fathy on 17/04/16.
 */
class CreateNewCenterFragmentTest : ActivityInstrumentationTestCase2<DashboardActivity?>(
    DashboardActivity::class.java
) {
    private var mActivity: DashboardActivity? = null
    @Throws(Exception::class)
    fun setUp() {
        super.setUp()
        mActivity = getActivity()
        openCreateCenter()
    }

    /**
     * - chooses "Create Center" from options menu
     * - checks the CrateNewCenterFragment is added
     */
    @SmallTest
    @Throws(InterruptedException::class)
    fun testOpenCreateCenter() {
        openCreateCenter()
        val createNewCenterFragment: CreateNewCenterFragment = mActivity.getSupportFragmentManager()
            .findFragmentById(R.id.container) as CreateNewCenterFragment
        assertNotNull(createNewCenterFragment)
        assertTrue(createNewCenterFragment.isAdded())
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testViewsVisible() {

        // initially, the center name edit test, office spinner and active checkbox are visible
        Espresso.onView(ViewMatchers.withId(R.id.et_center_name))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.sp_center_offices))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.cb_center_active_status))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // initially, the activation date is invisible but shows after checking the active checkbox
        Espresso.onView(ViewMatchers.withId(R.id.cb_center_active_status))
            .perform(ViewActions.scrollTo(), ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.tv_center_activationDate))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /**
     * checks that the center offcies spinner is loaded with data from the template
     */
    @SmallTest
    @Throws(InterruptedException::class)
    fun testSpinnerPopulated() {
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.sp_center_offices))
            .check(ViewAssertions.matches(hasChildren()))
    }

    /**
     * fills the template and presses submit
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testCreateCenter() {
        Thread.sleep(3000)

        // center name
        Espresso.onView(ViewMatchers.withId(R.id.et_center_name))
            .perform(ViewActions.scrollTo())
            .perform(ViewActions.typeText("myTestCenter"), ViewActions.closeSoftKeyboard())

        // office
        val spinnerOffice: Spinner = mActivity.findViewById(R.id.sp_center_offices) as Spinner
        val selectedOffice = spinnerOffice.getAdapter().getItem(2) as String
        Espresso.onView(ViewMatchers.withId(R.id.sp_center_offices))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withText(selectedOffice))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // activation date
        Espresso.onView(ViewMatchers.withId(R.id.cb_center_active_status))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = formatter.format(Date())
        Espresso.onView(ViewMatchers.withId(R.id.tv_center_activationDate))
            .check(ViewAssertions.matches(ViewMatchers.withText(currentDate)))

        // submit
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit))
            .perform(ViewActions.scrollTo(), ViewActions.click())
    }

    /**
     * chooses "Create Center" from the options menu
     */
    @Throws(InterruptedException::class)
    private fun openCreateCenter() {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        Espresso.onView(ViewMatchers.withText("Create Center"))
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