package com.mifos.mifosxdroid.tests;

import android.view.KeyEvent;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;

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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateNewClientFragmentTest {

    private ActivityScenario activityScenario;
    @Rule
    public ActivityTestRule<DashboardActivity> activityTestRule =
            new ActivityTestRule<>(DashboardActivity.class);

    @Before
    public void setUp() throws Exception {
        activityScenario = ActivityScenario.launch(DashboardActivity.class);
        openCreateClient();
    }

    @Test
    public void testViewsAreVisible() throws InterruptedException {

        Thread.sleep(3000);

        onView(withId(R.id.et_client_first_name))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.et_client_middle_name))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.et_client_last_name))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.et_client_mobile_no))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.et_client_external_id))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_gender))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.tv_dateofbirth))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_client_type))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_client_classification))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_offices))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.sp_staff))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withId(R.id.btn_submit))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.cb_client_active_status))
                .perform(scrollTo(), click())
                .check(matches(isDisplayed()));
        onView(withId(R.id.tv_submission_date))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testViewsAreNotNull() {
        assertNotNull(onView(withId(R.id.et_client_first_name)));
        assertNotNull(onView(withId(R.id.et_client_middle_name)));
        assertNotNull(onView(withId(R.id.et_client_last_name)));
        assertNotNull(onView(withId(R.id.et_client_mobile_no)));
        assertNotNull(onView(withId(R.id.et_client_external_id)));
        assertNotNull(onView(withId(R.id.sp_gender)));
        assertNotNull(onView(withId(R.id.tv_dateofbirth)));
        assertNotNull(onView(withId(R.id.sp_client_type)));
        assertNotNull(onView(withId(R.id.sp_client_classification)));
        assertNotNull(onView(withId(R.id.sp_offices)));
        assertNotNull(onView(withId(R.id.sp_staff)));
        assertNotNull(onView(withId(R.id.cb_client_active_status)));
        assertNotNull(onView(withId(R.id.tv_submission_date)));
        assertNotNull(onView(withId(R.id.btn_submit)));
    }

    @Test
    public void testOpenCreateClient() throws InterruptedException {
        CreateNewClientFragment createNewClientFragment
                = (CreateNewClientFragment) activityTestRule.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.container);

        assertNotNull(createNewClientFragment);
        assertTrue(createNewClientFragment.isAdded());
    }

    @Test
    public void testCreateClient() throws InterruptedException {

        Thread.sleep(3000);

        // First Name
        onView(withId(R.id.et_client_first_name))
                .perform(scrollTo())
                .perform(typeText("myFirstName"), closeSoftKeyboard());

        // Middle Name
        onView(withId(R.id.et_client_middle_name))
                .perform(scrollTo())
                .perform(typeText("myMiddleName"), closeSoftKeyboard());

        // Last Name
        onView(withId(R.id.et_client_last_name))
                .perform(scrollTo())
                .perform(typeText("myLastName"), closeSoftKeyboard());

        // Mobile Number
        onView(withId(R.id.et_client_mobile_no))
                .perform(scrollTo())
                .perform(typeText("987654321"), closeSoftKeyboard());

        // External Id
        onView(withId(R.id.et_client_external_id))
                .perform(scrollTo())
                .perform(typeText("123"), closeSoftKeyboard());

        // Gender
        Spinner spinnerGender = (Spinner) activityTestRule
                .getActivity().findViewById(R.id.sp_gender);
        String selectedGender = (String) spinnerGender.getAdapter().getItem(2);
        onView(withId(R.id.sp_gender))
                .perform(scrollTo(), click());
        onView(withText(selectedGender))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(isDisplayed()));

        // Client
        Spinner spinnerClient = (Spinner) activityTestRule
                .getActivity().findViewById(R.id.sp_client_type);
        String selectedClient = (String) spinnerClient.getAdapter().getItem(2);
        onView(withId(R.id.sp_client_type))
                .perform(scrollTo(), click());
        onView(withText(selectedClient))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(isDisplayed()));

        // Client Classification
        Spinner spinnerClientClassification = (Spinner) activityTestRule
                .getActivity().findViewById(R.id.sp_client_classification);
        String selectedClientClassification = (String) spinnerClientClassification
                .getAdapter().getItem(1);
        onView(withId(R.id.sp_client_classification))
                .perform(scrollTo(), click());
        onView(withText(selectedClientClassification))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(isDisplayed()));

        // Office
        Spinner spinnerOffice = (Spinner) activityTestRule
                .getActivity().findViewById(R.id.sp_offices);
        String selectedOffice = (String) spinnerOffice.getAdapter().getItem(0);
        onView(withId(R.id.sp_offices))
                .perform(scrollTo(), click());
        onView(withText(selectedOffice))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(isDisplayed()));

        // Staff
        Spinner spinnerStaff = (Spinner) activityTestRule
                .getActivity().findViewById(R.id.sp_staff);
        String selectedStaff = (String) spinnerStaff.getAdapter().getItem(0);
        onView(withId(R.id.sp_staff))
                .perform(scrollTo(), click());
        onView(withText(selectedStaff))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(isDisplayed()));

        // Activation Date
        onView(withId(R.id.cb_client_active_status))
                .perform(scrollTo(), click());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(new Date());
        onView(withId(R.id.tv_submission_date))
                .check(matches(withText(currentDate)));

        // Submit
        onView(withId(R.id.btn_submit))
                .perform(scrollTo(), click());
    }

    private void openCreateClient() throws InterruptedException {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        onView(withText("Create Client"))
                .perform(click());
    }
}
