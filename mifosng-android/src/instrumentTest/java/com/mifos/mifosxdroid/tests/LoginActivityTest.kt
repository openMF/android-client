/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.tests

import android.preference.PreferenceManager
import android.test.ActivityInstrumentationTestCase2
import android.view.View
import com.mifos.mifosxdroid.R
import com.mifos.utils.Constants

/**
 * Created by ishankhanna on 12/08/14.
 */
class LoginActivityTest : ActivityInstrumentationTestCase2<LoginActivity?>(
    LoginActivity::class.java
) {
    val TEST_URL_5: String = getActivity().getString(R.string.test_ip)
    var loginActivity: LoginActivity? = null
    var et_mifos_domain: EditText? = null
    var et_username: EditText? = null
    var tv_constructed_instance_url: TextView? = null
    @Throws(Exception::class)
    fun setUp() {
        super.setUp()
        loginActivity = getActivity()
        et_mifos_domain = loginActivity.findViewById(R.id.et_instanceURL) as EditText
        et_username = loginActivity.findViewById(R.id.et_username) as EditText
        tv_constructed_instance_url =
            loginActivity.findViewById(R.id.tv_constructed_instance_url) as TextView
    }

    @SmallTest
    fun testEditTextsAreNotNull() {
        assertNotNull(et_mifos_domain)
        assertNotNull(et_username)
    }

    @SmallTest
    fun testAllEditTextsAreVisible() {
        assertEquals(View.VISIBLE, et_mifos_domain.getVisibility())
    }

    @SmallTest
    fun testURLInstance1() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url)

        // Set URL and check the color of the message, it turns green
        // only if the URL matches the pattern specified
        enterMifosInstanceDomain(TEST_URL_1)
        assertEquals(
            loginActivity.getResources().getColor(R.color.green_light),
            tv_constructed_instance_url.getCurrentTextColor()
        )
    }

    @SmallTest
    fun testURLInstance2() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url)
        enterMifosInstanceDomain(TEST_URL_2)
        assertEquals(
            loginActivity.getResources().getColor(R.color.green_light),
            tv_constructed_instance_url.getCurrentTextColor()
        )
    }

    @SmallTest
    fun testURLInstance3() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url)
        enterMifosInstanceDomain(TEST_URL_3)
        assertEquals(
            loginActivity.getResources().getColor(R.color.green_light),
            tv_constructed_instance_url.getCurrentTextColor()
        )
    }

    @SmallTest
    fun testURLInstance4() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url)
        enterMifosInstanceDomain(TEST_URL_4)
        assertEquals(
            loginActivity.getResources().getColor(R.color.green_light),
            tv_constructed_instance_url.getCurrentTextColor()
        )
    }

    @SmallTest
    fun testURLInstance5() {
        //Test if TextView has been instantiated
        assertNotNull(tv_constructed_instance_url)
        enterMifosInstanceDomain(TEST_URL_5)
        assertEquals(
            loginActivity.getResources().getColor(R.color.green_light),
            tv_constructed_instance_url.getCurrentTextColor()
        )
    }

    @MediumTest
    fun testSaveLastAccessedInstanceDomainNameSavesProvidedString() {
        PrefManager.instanceDomain = TEST_URL_1
        assertEquals(TEST_URL_1, PrefManager.instanceDomain)
        PrefManager.instanceDomain = TEST_URL_2
        assertEquals(TEST_URL_2, PrefManager.instanceDomain)
    }

    @MediumTest
    @Suppress // TODO: Fix ComparisonFailure: expected:<[demo.mifos.org]> but was:<[www.google.com]>
    fun testValidateUserInputsSavesValidDomainToSharedProperties() {
        saveLastAccessedInstanceDomainName(TEST_URL_2)
        enterMifosInstanceDomain(TEST_URL_1)
        getActivity().runOnUiThread(Runnable { getActivity().validateUserInputs() })
        getInstrumentation().waitForIdleSync()
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext())
        assertEquals(TEST_URL_1, sharedPreferences.getString(Constants.INSTANCE_URL_KEY, ""))
    }

    @MediumTest
    @Suppress // TODO: Fix expected:<https://demo.[mifos.org:80]/mifosng-provider/ap...>
    // but was:<https://demo.[openmf.org]/mifosng-provider/ap...>
    fun testValidateUserInputsSetsAPIinstanceUrl() {
        saveLastAccessedInstanceDomainName(TEST_URL_2)
        enterMifosInstanceDomain(TEST_URL_1)
        getActivity().runOnUiThread(Runnable { getActivity().validateUserInputs() })
        getInstrumentation().waitForIdleSync()
    }

    @SmallTest
    fun testMoreOptionsDisplaysOfflineMenuItem() {
        Espresso.onView(ViewMatchers.withContentDescription("More options"))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(Matchers.`is`<String>(Matchers.startsWith("Offline"))))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun enterMifosInstanceDomain(domain: String) {
        clearMifosDomainTextInputField()
        getInstrumentation().sendStringSync(domain)
        getInstrumentation().waitForIdleSync()
    }

    private fun clearMifosDomainTextInputField() {
        getActivity().runOnUiThread(Runnable {
            et_mifos_domain.setText("")
            et_mifos_domain.requestFocus()
        })
        getInstrumentation().waitForIdleSync()
    }

    private fun saveLastAccessedInstanceDomainName(domain: String) {
        getInstrumentation().runOnMainSync(Runnable { PrefManager.instanceDomain = domain })
        getInstrumentation().waitForIdleSync()
    }

    companion object {
        const val TEST_URL_1 = "demo.mifos.org"
        const val TEST_URL_2 = "www.google.com"
        const val TEST_URL_3 = "this.is.valid.url"
        const val TEST_URL_4 = "yahoo.in"
    }
}