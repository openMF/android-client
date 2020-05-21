package com.mifos.mifosxdroid.tests;

import android.view.KeyEvent;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateNewCenterFragmentTest {

    private ActivityScenario activityScenario;
    @Rule
    public ActivityTestRule<DashboardActivity> activityTestRule =
            new ActivityTestRule<>(DashboardActivity.class);

    @Before
    public void setUp() throws Exception {
        activityScenario = ActivityScenario.launch(DashboardActivity.class);
        openCreateCenter();
    }

    @Test
    public void testViewsAreVisible() throws InterruptedException {

        Thread.sleep(3000);

        onView(withId(R.id.et_center_name))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_center_offices))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.cb_center_active_status))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.btn_submit))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.cb_center_active_status))
                .perform(scrollTo(), click())
                .check(matches(isChecked()));
        onView(withId(R.id.tv_center_activationDate))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testViewsAreNotNull() {
        assertNotNull(onView(withId(R.id.et_center_name)));
        assertNotNull(onView(withId(R.id.sp_center_offices)));
        assertNotNull(onView(withId(R.id.cb_center_active_status)));
        assertNotNull(onView(withId(R.id.btn_submit)));
        assertNotNull(onView(withId(R.id.tv_center_activationDate)));
    }

    @Test
    public void testOpenCreateCenter() throws InterruptedException {
        CreateNewCenterFragment createNewCenterFragment
                = (CreateNewCenterFragment) activityTestRule.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.container);

        assertNotNull(createNewCenterFragment);
        assertTrue(createNewCenterFragment.isAdded());
    }

    @Test
    public void testSpinnerPopulated() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.sp_center_offices))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void testCreateCenter() throws InterruptedException {

        Thread.sleep(3000);

        // Center Name
        onView(withId(R.id.et_center_name))
                .perform(scrollTo())
                .perform(typeText("myTestCenter"), closeSoftKeyboard());

        // Office
        Spinner spinnerOffice = (Spinner) activityTestRule
                .getActivity().findViewById(R.id.sp_center_offices);
        String selectedOffice = (String) spinnerOffice.getAdapter().getItem(2);
        onView(withId(R.id.sp_center_offices))
                .perform(scrollTo(), click());
        onView(withText(selectedOffice))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(isDisplayed()));

        // Activation Date
        onView(withId(R.id.cb_center_active_status))
                .perform(scrollTo(), click());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(new Date());
        onView(withId(R.id.tv_center_activationDate))
                .check(matches(withText(currentDate)));

        // Submit
        onView(withId(R.id.btn_submit))
                .perform(scrollTo(), click());
    }

    private void openCreateCenter() throws InterruptedException {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        onView(withText("Create Center"))
                .perform(click());
    }
}
