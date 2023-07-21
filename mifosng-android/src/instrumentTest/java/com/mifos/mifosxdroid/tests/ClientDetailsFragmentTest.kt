package com.mifos.mifosxdroid.tests

import android.test.ActivityInstrumentationTestCase2
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import com.mifos.mifosxdroid.R
import com.mifos.utils.Constants

/**
 * Created by Gabriel Esteban on 07/12/14.
 */
@Suppress // TODO: Fix NPE

class ClientDetailsFragmentTest : ActivityInstrumentationTestCase2<ClientActivity?>(
    ClientActivity::class.java
) {
    var clientActivity: ClientActivity? = null
    var detailsFragment: ClientDetailsFragment? = null
    var iv_client_image: ImageView? = null
    var tv_full_name: TextView? = null
    var tbl_client_details: TableLayout? = null
    var loans: RelativeLayout? = null
    var savings: RelativeLayout? = null
    var recurring: RelativeLayout? = null
    @Throws(Exception::class)
    protected fun setUp() {
        super.setUp()
        val clientActivityIntent = Intent()
        clientActivityIntent.putExtra(Constants.CLIENT_ID, "000000001")
        setActivityIntent(clientActivityIntent)
        clientActivity = getActivity()
        //waiting for the API
        Thread.sleep(2000)
        detailsFragment = getActivity().getSupportFragmentManager()
            .findFragmentByTag(FragmentConstants.FRAG_CLIENT_DETAILS) as ClientDetailsFragment
        iv_client_image = clientActivity.findViewById(R.id.iv_clientImage)
        tv_full_name = clientActivity.findViewById(R.id.tv_fullName) as TextView
        tbl_client_details = clientActivity.findViewById(R.id.tbl_clientDetails) as TableLayout
        loans = clientActivity.findViewById(R.id.account_accordion_section_loans) as RelativeLayout
        savings =
            clientActivity.findViewById(R.id.account_accordion_section_savings) as RelativeLayout
        recurring =
            clientActivity.findViewById(R.id.account_accordion_section_recurring) as RelativeLayout
    }

    @SmallTest
    fun testFragmentIsNotNull() {
        assertNotNull(detailsFragment)
    }

    @SmallTest
    fun testViewsAreNotNull() {
        assertNotNull(iv_client_image)
        assertNotNull(tv_full_name)
        assertNotNull(tbl_client_details)
        assertNotNull(loans)
        assertNotNull(savings)
        assertNotNull(recurring)
    }

    @SmallTest
    fun testViewsAreOnTheScreen() {
        val decorView: View = clientActivity.getWindow().getDecorView()
        ViewAsserts.assertOnScreen(decorView, iv_client_image)
        ViewAsserts.assertOnScreen(decorView, tv_full_name)
        ViewAsserts.assertOnScreen(decorView, tbl_client_details)
        ViewAsserts.assertOnScreen(decorView, loans)
        ViewAsserts.assertOnScreen(decorView, savings)
        ViewAsserts.assertOnScreen(decorView, recurring)
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testClientDocumentsFragmentShowed() {
        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        //        getInstrumentation().invokeMenuActionSync(clientActivity, ClientDetailsFragment
// .MENU_ITEM_DOCUMENTS, 0);

        //if something is wrong, invokeMenuActionSync will take an exception

        //waiting for the API
        Thread.sleep(2000)
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testClientIdentifiersFragmentShowed() {
        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        //        getInstrumentation().invokeMenuActionSync(clientActivity, ClientDetailsFragment
// .MENU_ITEM_IDENTIFIERS, 0);

        //if something is wrong, invokeMenuActionSync will take an exception

        //waiting for the API
        Thread.sleep(2000)
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }
}