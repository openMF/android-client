/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Spinner;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.CreateNewGroupFragment;
import com.mifos.mifosxdroid.online.DashboardActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by ahmed fathy on 20/04/16.
 */
public class CreateNewGroupFragmentTest extends ActivityInstrumentationTestCase2<DashboardActivity> {

    private DashboardActivity mActivity;

    public CreateNewGroupFragmentTest() {
        super(DashboardActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        openCreateGroup();
    }

    /**
     * - chooses "Create Group" from options menu
     * - checks the CrateNewGroupFragment is added
     */
    @SmallTest
    public void testOpenCreateGroup() throws InterruptedException {

        CreateNewGroupFragment createNewGroupFragment
                = (CreateNewGroupFragment) mActivity.getSupportFragmentManager()
                .findFragmentById(R.id.container);

        assertNotNull(createNewGroupFragment);
        assertTrue(createNewGroupFragment.isAdded());
    }


    @SmallTest
    public void testViewsVisible() throws InterruptedException {

        // initially, the group name, office spinner. submission date,
        // external id, active checkbox and submit button are visible
        onView(withId(R.id.et_group_name))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_group_offices))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.tv_group_submission_date))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.et_group_external_id))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.cb_group_active_status))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.bt_submit))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        // initially, the activation date is invisible but shows after checking the active checkbox
        onView(withId(R.id.cb_group_active_status))
                .perform(scrollTo(), click())
                .check(matches(isChecked()));
        onView(withId(R.id.tv_group_activationDate))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    /**
     * checks that the office spinner is loaded with data from the template
     */
    @SmallTest
    public void testSpinnerPopulated() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.sp_group_offices))
                .check(matches(hasChildren()));
    }


    /**
     * fills the template and presses submit
     */
    @MediumTest
    public void testCreateGroup() throws InterruptedException {
        Thread.sleep(3000);

        // group name
        onView(withId(R.id.et_group_name))
                .perform(scrollTo())
                .perform(typeText("myTestGroup"), closeSoftKeyboard());

        // office
        Spinner spinnerOffice = (Spinner) mActivity.findViewById(R.id.sp_group_offices);
        if (spinnerOffice.getAdapter().getCount() > 2) {
            String selectedOffice = (String) spinnerOffice.getAdapter().getItem(2);
            onView(withId(R.id.sp_group_offices))
                    .perform(scrollTo(), click());
            onView(withText(selectedOffice))
                    .check(matches(isDisplayed()))
                    .perform(click())
                    .check(matches(isDisplayed()));
        }

        // submission date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(new Date());
        onView(withId(R.id.tv_group_submission_date))
                .perform(scrollTo())
                .check(matches(withText(currentDate)));

        // external id
        onView(withId(R.id.et_group_external_id))
                .perform(scrollTo())
                .perform(typeText("123"), closeSoftKeyboard());

        // activation date
        onView(withId(R.id.cb_group_active_status))
                .perform(scrollTo(), click());
        onView(withId(R.id.tv_group_activationDate))
                .check(matches(withText(currentDate)));

        // submit
        onView(withId(R.id.bt_submit))
                .perform(scrollTo(), click());
    }


    /**
     * chooses "Create Group" from the options menu
     */
    private void openCreateGroup() throws InterruptedException {

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        onView(withText("Create Group"))
                .perform(click());
    }


    /**
     * checks that the spinner is loaded with 1 or more items
     */
    public static Matcher<View> hasChildren() {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return ((Spinner) view).getAdapter().getCount() > 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("The spinner has no children");
            }
        };
    }

}