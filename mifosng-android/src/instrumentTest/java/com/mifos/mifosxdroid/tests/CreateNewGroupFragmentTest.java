package com.mifos.mifosxdroid.tests;

import android.view.KeyEvent;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;

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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateNewGroupFragmentTest {

    private ActivityScenario activityScenario;
    @Rule
    public ActivityTestRule<DashboardActivity> activityTestRule =
            new ActivityTestRule<>(DashboardActivity.class);

    @Before
    public void setUp() throws Exception {
        activityScenario = ActivityScenario.launch(DashboardActivity.class);
        openCreateGroup();
    }

    @Test
    public void testViewsAreVisible() throws InterruptedException {

        Thread.sleep(3000);

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
        onView(withId(R.id.btn_submit))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.cb_group_active_status))
                .perform(scrollTo(), click())
                .check(matches(isDisplayed()));
        onView(withId(R.id.tv_group_activationDate))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testViewsAreNotNull() throws InterruptedException {
        assertNotNull(onView(withId(R.id.et_group_name)));
        assertNotNull(onView(withId(R.id.sp_group_offices)));
        assertNotNull(onView(withId(R.id.tv_group_submission_date)));
        assertNotNull(onView(withId(R.id.et_group_external_id)));
        assertNotNull(onView(withId(R.id.cb_group_active_status)));
        assertNotNull(onView(withId(R.id.btn_submit)));
        assertNotNull(onView(withId(R.id.tv_group_activationDate)));
    }

    @Test
    public void testOpenCreateGroup() throws InterruptedException {

        CreateNewGroupFragment createNewGroupFragment
                = (CreateNewGroupFragment) activityTestRule.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.container);

        assertNotNull(createNewGroupFragment);
        assertTrue(createNewGroupFragment.isAdded());

    }

    @Test
    public void testSpinnerPopulated() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.sp_group_offices)).
                check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void testCreateGroup() throws InterruptedException {
        Thread.sleep(3000);

        // Group Name
        onView(withId(R.id.et_group_name))
                .perform(scrollTo())
                .perform(typeText("myTestGroup"), closeSoftKeyboard());

        // Office
        Spinner spinnerOffice = activityTestRule.getActivity().findViewById(R.id.sp_group_offices);
        if (spinnerOffice.getAdapter().getCount() > 2) {
            String selectedOffice = (String) spinnerOffice.getAdapter().getItem(2);
            onView(withId(R.id.sp_group_offices))
                    .perform(scrollTo(), click());
            onView(withText(selectedOffice))
                    .check(matches(isDisplayed()))
                    .perform(click())
                    .check(matches(isDisplayed()));
        }

        // Submission Date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(new Date());
        onView(withId(R.id.tv_group_submission_date))
                .perform(scrollTo())
                .check(matches(withText(currentDate)));

        // External Id
        onView(withId(R.id.et_group_external_id))
                .perform(scrollTo())
                .perform(typeText("123"), closeSoftKeyboard());

        // Activation Date
        onView(withId(R.id.cb_group_active_status))
                .perform(scrollTo(), click());
        onView(withId(R.id.tv_group_activationDate))
                .check(matches(withText(currentDate)));

        // Submit
        onView(withId(R.id.btn_submit))
                .perform(scrollTo(), click());
    }

    private void openCreateGroup() throws InterruptedException {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        onView(withText("Create Group"))
                .perform(click());
    }
}
