/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.tests

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.mifos.mifosxdroid.R
import com.mifos.utils.Constants

/**
 * Created by ahmed fathy on 10/4/16.
 */
class ClientChargesTest : ActivityInstrumentationTestCase2<ClientActivity?>(
    ClientActivity::class.java
) {
    /* fields */
    var activity: ClientActivity? = null
    @Throws(Exception::class)
    protected fun setUp() {
        super.setUp()

        // set the intent with the user id
        val intent = Intent()
        intent.putExtra(Constants.CLIENT_ID, 1)
        setActivityIntent(intent)
        activity = getActivity()
    }

    /**
     * open client activity
     * open charges
     * check the charges fragment is there
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testChargesFragmentVisible() {

        // wait for the API
        Thread.sleep(3000)
        openCharges()
        Thread.sleep(2000)

        // check the fragment is there
        val clientChargesFragment: ClientChargeFragment = activity
            .getSupportFragmentManager()
            .findFragmentById(R.id.container) as ClientChargeFragment
        assertNotNull(clientChargesFragment)
        assertTrue(clientChargesFragment.isAdded())
    }

    /**
     * opens client activity
     * opens charges
     * chooses create new charge
     * checks the ChargeDialogFragment is there
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testCreateChargesVisible() {

        // wait for the API
        Thread.sleep(3000)
        openCreateNewCharge()
        Thread.sleep(2000)

        // check the fragment is there
        val chargeDialogFragment: ChargeDialogFragment = activity
            .getSupportFragmentManager()
            .findFragmentByTag("Charge Dialog Fragment") as ChargeDialogFragment
        assertNotNull(chargeDialogFragment)
        assertTrue(chargeDialogFragment.isAdded())
    }

    /**
     * opens client activity
     * opens charges
     * chooses create new charge
     * enter the new charge details
     * press submit
     */
    @Throws(InterruptedException::class)
    fun testCreateNewCharge() {

        // open the dialog
        openCreateNewCharge()
        val chargeDialogFragment: ChargeDialogFragment = activity
            .getSupportFragmentManager()
            .findFragmentByTag("Charge Dialog Fragment") as ChargeDialogFragment

        // enter the date
        Espresso.onView(ViewMatchers.withId(R.id.amount_due_charge))
            .perform(ViewActions.typeText("12"))
        Espresso.onView(ViewMatchers.withId(R.id.et_date)).perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.et_date))
            .perform(ViewActions.typeText("08-04-2018"))

        // press save
        Espresso.onView(ViewMatchers.withId(R.id.bt_save_charge)).perform(ViewActions.click())
        Thread.sleep(2000)
    }

    /**
     * open charges fragment by choosing the "charges" options menu item
     */
    private fun openCharges() {
        val clientDetailsFragment: ClientDetailsFragment = activity
            .getSupportFragmentManager()
            .findFragmentById(R.id.container) as ClientDetailsFragment
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        chooseOptionItem(clientDetailsFragment, R.id.charges)
    }

    /**
     * open charges fragment then choose the "new" options menu item
     */
    @Throws(InterruptedException::class)
    private fun openCreateNewCharge() {
        openCharges()
        Thread.sleep(2000)
        val clientChargeFragment: ClientChargeFragment = activity
            .getSupportFragmentManager()
            .findFragmentById(R.id.container) as ClientChargeFragment
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        chooseOptionItem(clientChargeFragment, ClientChargeFragment.MENU_ITEM_ADD_NEW_CHARGES)
    }

    /**
     * a work around to choose from an options menu made from the fragment not the activity
     * so getInstrumentation().invokeMenuActionSync(getActivity(), R.id.charges, 0) wont work
     */
    fun chooseOptionItem(fragment: Fragment, id: Int) {
        val item = ActionMenuItem(activity, 0, id, 0, 0, "")
        fragment.onOptionsItemSelected(item)
    }
}