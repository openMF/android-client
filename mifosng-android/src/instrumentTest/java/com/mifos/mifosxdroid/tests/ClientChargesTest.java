/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.ActionMenuItem;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.KeyEvent;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.dialogfragments.ChargeDialogFragment;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.mifosxdroid.online.ClientChargeFragment;
import com.mifos.mifosxdroid.online.ClientDetailsFragment;
import com.mifos.utils.Constants;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ahmed fathy on 10/4/16.
 */
public class ClientChargesTest extends ActivityInstrumentationTestCase2<ClientActivity> {

    /* fields */
    ClientActivity activity;

    public ClientChargesTest() {
        super(ClientActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // set the intent with the user id
        Intent intent = new Intent();
        intent.putExtra(Constants.CLIENT_ID, 1);
        setActivityIntent(intent);
        activity = getActivity();
    }

    /**
     * open client activity
     * open charges
     * check the charges fragment is there
     */
    @MediumTest
    public void testChargesFragmentVisible() throws InterruptedException {

        // wait for the API
        Thread.sleep(3000);
        openCharges();
        Thread.sleep(2000);

        // check the fragment is there
        ClientChargeFragment clientChargesFragment =
                (ClientChargeFragment) activity
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.container);
        assertNotNull(clientChargesFragment);
        assertTrue(clientChargesFragment.isAdded());
    }

    /**
     * opens client activity
     * opens charges
     * chooses create new charge
     * checks the ChargeDialogFragment is there
     */
    @MediumTest
    public void testCreateChargesVisible() throws InterruptedException {

        // wait for the API
        Thread.sleep(3000);
        openCreateNewCharge();
        Thread.sleep(2000);

        // check the fragment is there
        ChargeDialogFragment chargeDialogFragment =
                (ChargeDialogFragment) activity
                        .getSupportFragmentManager()
                        .findFragmentByTag("Charge Dialog Fragment");
        assertNotNull(chargeDialogFragment);
        assertTrue(chargeDialogFragment.isAdded());
    }

    /**
     * opens client activity
     * opens charges
     * chooses create new charge
     * enter the new charge details
     * press submit
     */
    public void testCreateNewCharge() throws InterruptedException {

        // open the dialog
        openCreateNewCharge();
        ChargeDialogFragment chargeDialogFragment =
                (ChargeDialogFragment) activity
                        .getSupportFragmentManager()
                        .findFragmentByTag("Charge Dialog Fragment");

        // enter the date
        onView(withId(R.id.amount_due_charge)).perform(typeText("12"));
        onView(withId(R.id.et_date)).perform(clearText());
        onView(withId(R.id.et_date)).perform(typeText("08-04-2018"));

        // press save
        onView(withId(R.id.bt_save_charge)).perform(click());
        Thread.sleep(2000);

    }

    /**
     * open charges fragment by choosing the "charges" options menu item
     */
    private void openCharges() {
        ClientDetailsFragment clientDetailsFragment =
                (ClientDetailsFragment) activity
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.container);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        chooseOptionItem(clientDetailsFragment, R.id.charges);
    }

    /**
     * open charges fragment then choose the "new" options menu item
     */
    private void openCreateNewCharge() throws InterruptedException {
        openCharges();
        Thread.sleep(2000);

        ClientChargeFragment clientChargeFragment = (ClientChargeFragment) activity
                .getSupportFragmentManager()
                .findFragmentById(R.id.container);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        chooseOptionItem(clientChargeFragment, ClientChargeFragment.MENU_ITEM_ADD_NEW_CHARGES);
    }

    /**
     * a work around to choose from an options menu made from the fragment not the activity
     * so getInstrumentation().invokeMenuActionSync(getActivity(), R.id.charges, 0) wont work
     */
    public void chooseOptionItem(Fragment fragment, int id) {
        ActionMenuItem item = new ActionMenuItem(activity, 0, id, 0, 0, "");
        fragment.onOptionsItemSelected(item);
    }

}
