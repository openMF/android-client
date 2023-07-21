package com.mifos.mifosxdroid.tests

import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.FragmentTransaction
import com.mifos.mifosxdroid.R
import com.mifos.utils.Constants

/**
 * Created by Gabriel Esteban on 07/12/14.
 */
@Suppress // TODO: Fix NPE

class SavingsAccountSummaryFragmentTest : ActivityInstrumentationTestCase2<ClientActivity?>(
    ClientActivity::class.java
) {
    var clientActivity: ClientActivity? = null
    var tv_clientName: TextView? = null
    var quickContactBadge: QuickContactBadge? = null
    var tv_savingsProductName: TextView? = null
    var tv_savingsAccountNumber: TextView? = null
    var tv_savingsAccountBalance: TextView? = null
    var tv_totalDeposits: TextView? = null
    var tv_totalWithdrawals: TextView? = null
    var lv_Transactions: ListView? = null
    var tv_interestEarned: TextView? = null
    var bt_deposit: Button? = null
    var bt_withdrawal: Button? = null
    @Throws(Exception::class)
    protected fun setUp() {
        super.setUp()

        //Entering to this client for testing
        val clientActivityIntent = Intent()
        clientActivityIntent.putExtra(Constants.CLIENT_ID, "000001026")
        setActivityIntent(clientActivityIntent)
        clientActivity = getActivity()
        /*
        Test made with the following constructor on DepositType

        public DepositType(Integer id, String code, String value) {
            this.id = id;
            this.code = code;
            this.value = value;
        }
         */
        val depositType: DepositType? = null
        //depositType = new DepositType(100, "depositAccountType.savingsDeposit", "Savings");

        //Moving to SavingsAccountSummaryFragment with the following id and account type
        val savingsAccountSummaryFragment: SavingsAccountSummaryFragment =
            SavingsAccountSummaryFragment.newInstance(419, depositType)
        val fragmentTransaction: FragmentTransaction = getActivity().getSupportFragmentManager()
            .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.global_container, savingsAccountSummaryFragment).commit()

        //waiting for the API
        Thread.sleep(8000)

        //instantiating the views that we're going to test@BindView(R.id.tv_clientName)
        tv_clientName = clientActivity.findViewById(R.id.tv_clientName) as TextView
        quickContactBadge =
            clientActivity.findViewById(R.id.quickContactBadge_client) as QuickContactBadge
        tv_savingsProductName =
            clientActivity.findViewById(R.id.tv_savings_product_short_name) as TextView
        tv_savingsAccountNumber =
            clientActivity.findViewById(R.id.tv_savingsAccountNumber) as TextView
        tv_savingsAccountBalance =
            clientActivity.findViewById(R.id.tv_savings_account_balance) as TextView
        tv_totalDeposits = clientActivity.findViewById(R.id.tv_total_deposits) as TextView
        tv_totalWithdrawals = clientActivity.findViewById(R.id.tv_total_withdrawals) as TextView
        lv_Transactions = clientActivity.findViewById(R.id.lv_savings_transactions)
        tv_interestEarned = clientActivity.findViewById(R.id.tv_interest_earned) as TextView
        bt_deposit = clientActivity.findViewById(R.id.bt_deposit)
        bt_withdrawal = clientActivity.findViewById(R.id.bt_withdrawal)
    }

    //we are not going to test if fragment is null this time,
    //because we've started it from this test, so we wouldn't be testing the app
    @SmallTest
    fun testViewsAreNotNull() {
        assertNotNull(tv_clientName)
        assertNotNull(quickContactBadge)
        assertNotNull(tv_savingsProductName)
        assertNotNull(tv_savingsAccountNumber)
        assertNotNull(tv_savingsAccountBalance)
        assertNotNull(tv_totalDeposits)
        assertNotNull(tv_totalWithdrawals)
        assertNotNull(lv_Transactions)
        assertNotNull(tv_interestEarned)
        assertNotNull(bt_deposit)
        assertNotNull(bt_withdrawal)
    }

    @SmallTest
    fun testViewsAreOnTheScreen() {
        val decorView: View = clientActivity.getWindow().getDecorView()
        ViewAsserts.assertOnScreen(decorView, tv_clientName)
        ViewAsserts.assertOnScreen(decorView, quickContactBadge)
        ViewAsserts.assertOnScreen(decorView, tv_savingsProductName)
        ViewAsserts.assertOnScreen(decorView, tv_savingsAccountNumber)
        ViewAsserts.assertOnScreen(decorView, tv_savingsAccountBalance)
        ViewAsserts.assertOnScreen(decorView, tv_totalDeposits)
        ViewAsserts.assertOnScreen(decorView, tv_totalWithdrawals)
        ViewAsserts.assertOnScreen(decorView, lv_Transactions)
        ViewAsserts.assertOnScreen(decorView, tv_interestEarned)
        ViewAsserts.assertOnScreen(decorView, bt_deposit)
        ViewAsserts.assertOnScreen(decorView, bt_withdrawal)
    }

    /**
     * Should be tested alone because sometimes it can cause problems with the Application Context.
     *
     *
     * Maybe it should be reviewed the [DocumentListFragment],
     * because is who is throwing out a NullPointerException after closing the fragment. The
     * method who
     * throws the exception is inflateDocumentList on the API request.
     *
     *
     * Here the logcat output:
     * java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.Context
     * android.support.v4.app.FragmentActivity.getApplicationContext()' on a null object reference
     * at com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryFragment$1.success
     * (SavingsAccountSummaryFragment.java:201)
     * at com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryFragment$1.success
     * (SavingsAccountSummaryFragment.java:169)
     * at retrofit.CallbackRunnable$1.run(CallbackRunnable.java:41)
     * at android.os.Handler.handleCallback(Handler.java:739)
     * at android.os.Handler.dispatchMessage(Handler.java:95)
     * at android.os.Looper.loop(Looper.java:135)
     * at android.app.ActivityThread.main(ActivityThread.java:5221)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at java.lang.reflect.Method.invoke(Method.java:372)
     * at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:899)
     * at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:694)
     *
     * @throws InterruptedException
     */
    @SmallTest
    @Throws(InterruptedException::class)
    fun testClientDocumentsFragmentShowed() {
        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU)
        getInstrumentation().invokeMenuActionSync(
            clientActivity,
            SavingsAccountSummaryFragment.MENU_ITEM_DOCUMENTS,
            0
        )

        //if something is wrong, invokeMenuActionSync will take an exception

        //waiting for the API
        Thread.sleep(4000)
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testMakeWithdrawalButton() {
        TouchUtils.clickView(this, bt_withdrawal)

        //waiting for the API
        Thread.sleep(4000)

        //Try if cancel button is onScreen, if true the fragment have started
        val decorView: View = clientActivity.getWindow().getDecorView()
        val cancel = clientActivity.findViewById(R.id.bt_cancelTransaction) as Button
        ViewAsserts.assertOnScreen(decorView, cancel)

        //return to the savings fragment
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }

    @SmallTest
    @Throws(InterruptedException::class)
    fun testMakeDepositButton() {
        TouchUtils.clickView(this, bt_deposit)

        //waiting for the API
        Thread.sleep(4000)

        //Try if cancel button is onScreen, if true the fragment have started
        val decorView: View = clientActivity.getWindow().getDecorView()
        val cancel = clientActivity.findViewById(R.id.bt_cancelTransaction) as Button
        ViewAsserts.assertOnScreen(decorView, cancel)

        //return to the savings fragment
        this.sendKeys(KeyEvent.KEYCODE_BACK)
    }
}