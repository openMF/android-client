package com.mifos.mifosxdroid.tests;

import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.mifosxdroid.online.ClientDetailsFragment;
import com.mifos.mifosxdroid.online.SavingsAccountSummaryFragment;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

/**
 * Created by Gabriel Esteban on 07/12/14.
 */
public class SavingsAccountSummaryFragmentTest extends ActivityInstrumentationTestCase2<ClientActivity> {

    ClientActivity clientActivity;

    TextView tv_clientName;
    QuickContactBadge quickContactBadge;
    TextView tv_savingsProductName;
    TextView tv_savingsAccountNumber;
    TextView tv_savingsAccountBalance;
    TextView tv_totalDeposits;
    TextView tv_totalWithdrawals;
    ListView lv_Transactions;
    TextView tv_interestEarned;
    Button bt_deposit;
    Button bt_withdrawal;

    public SavingsAccountSummaryFragmentTest() {
        super(ClientActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //Entering to this client for testing
        Intent clientActivityIntent = new Intent();
        clientActivityIntent.putExtra(Constants.CLIENT_ID, "000001026");
        setActivityIntent(clientActivityIntent);

        Constants.applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
        clientActivity = getActivity();
        /*
        Test made with the following constructor on DepositType

        public DepositType(Integer id, String code, String value) {
            this.id = id;
            this.code = code;
            this.value = value;
        }
         */
        DepositType depositType = null;
        //depositType = new DepositType(100, "depositAccountType.savingsDeposit", "Savings");

        //Moving to SavingsAccountSummaryFragment with the following id and account type
        SavingsAccountSummaryFragment savingsAccountSummaryFragment
                = SavingsAccountSummaryFragment.newInstance(419, depositType);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container, savingsAccountSummaryFragment).commit();

        //waiting for the API
        Thread.sleep(8000);

        //instantiating the views that we're going to test@InjectView(R.id.tv_clientName)
        tv_clientName = (TextView) clientActivity.findViewById(R.id.tv_clientName);
        quickContactBadge = (QuickContactBadge) clientActivity.findViewById(R.id.quickContactBadge_client);
        tv_savingsProductName = (TextView) clientActivity.findViewById(R.id.tv_savings_product_short_name);
        tv_savingsAccountNumber = (TextView) clientActivity.findViewById(R.id.tv_savingsAccountNumber);
        tv_savingsAccountBalance = (TextView) clientActivity.findViewById(R.id.tv_savings_account_balance);
        tv_totalDeposits = (TextView) clientActivity.findViewById(R.id.tv_total_deposits);
        tv_totalWithdrawals = (TextView) clientActivity.findViewById(R.id.tv_total_withdrawals);
        lv_Transactions = (ListView) clientActivity.findViewById(R.id.lv_savings_transactions);
        tv_interestEarned = (TextView) clientActivity.findViewById(R.id.tv_interest_earned);
        bt_deposit = (Button) clientActivity.findViewById(R.id.bt_deposit);
        bt_withdrawal = (Button) clientActivity.findViewById(R.id.bt_withdrawal);
    }

    //we are not going to test if fragment is null this time,
    //because we've started it from this test, so we wouldn't be testing the app

    @SmallTest
    public void testViewsAreNotNull() {
        assertNotNull(tv_clientName);
        assertNotNull(quickContactBadge);
        assertNotNull(tv_savingsProductName);
        assertNotNull(tv_savingsAccountNumber);
        assertNotNull(tv_savingsAccountBalance);
        assertNotNull(tv_totalDeposits);
        assertNotNull(tv_totalWithdrawals);
        assertNotNull(lv_Transactions);
        assertNotNull(tv_interestEarned);
        assertNotNull(bt_deposit);
        assertNotNull(bt_withdrawal);
    }

    @SmallTest
    public void testViewsAreOnTheScreen() {
        final View decorView = clientActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, tv_clientName);
        ViewAsserts.assertOnScreen(decorView, quickContactBadge);
        ViewAsserts.assertOnScreen(decorView, tv_savingsProductName);
        ViewAsserts.assertOnScreen(decorView, tv_savingsAccountNumber);
        ViewAsserts.assertOnScreen(decorView, tv_savingsAccountBalance);
        ViewAsserts.assertOnScreen(decorView, tv_totalDeposits);
        ViewAsserts.assertOnScreen(decorView, tv_totalWithdrawals);
        ViewAsserts.assertOnScreen(decorView, lv_Transactions);
        ViewAsserts.assertOnScreen(decorView, tv_interestEarned);
        ViewAsserts.assertOnScreen(decorView, bt_deposit);
        ViewAsserts.assertOnScreen(decorView, bt_withdrawal);
    }

    /**
     * Should be tested alone because sometimes it can cause problems with the Application Context.
     *
     * Maybe it should be reviewed the {@link com.mifos.mifosxdroid.online.DocumentListFragment},
     * because is who is throwing out a NullPointerException after closing the fragment. The method who
     * throws the exception is inflateDocumentList on the API request.
     *
     * Here the logcat output:
     * java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.Context android.support.v4.app.FragmentActivity.getApplicationContext()' on a null object reference
     *        at com.mifos.mifosxdroid.online.SavingsAccountSummaryFragment$1.success(SavingsAccountSummaryFragment.java:201)
     *        at com.mifos.mifosxdroid.online.SavingsAccountSummaryFragment$1.success(SavingsAccountSummaryFragment.java:169)
     *        at retrofit.CallbackRunnable$1.run(CallbackRunnable.java:41)
     *        at android.os.Handler.handleCallback(Handler.java:739)
     *        at android.os.Handler.dispatchMessage(Handler.java:95)
     *        at android.os.Looper.loop(Looper.java:135)
     *        at android.app.ActivityThread.main(ActivityThread.java:5221)
     *        at java.lang.reflect.Method.invoke(Native Method)
     *        at java.lang.reflect.Method.invoke(Method.java:372)
     *        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:899)
     *        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:694)
     *
     * @throws InterruptedException
     */
    @SmallTest
    public void testClientDocumentsFragmentShowed() throws InterruptedException {
        //clicking the button
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(clientActivity, SavingsAccountSummaryFragment.MENU_ITEM_DOCUMENTS, 0);

        //if something is wrong, invokeMenuActionSync will take an exception

        //waiting for the API
        Thread.sleep(4000);

        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @SmallTest
    public void testMakeWithdrawalButton() throws InterruptedException {
        TouchUtils.clickView(this, bt_withdrawal);

        //waiting for the API
        Thread.sleep(4000);

        //Try if cancel button is onScreen, if true the fragment have started
        final View decorView = clientActivity.getWindow().getDecorView();
        final Button cancel = (Button) clientActivity.findViewById(R.id.bt_cancelTransaction);
        ViewAsserts.assertOnScreen(decorView, cancel);

        //return to the savings fragment
        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }

    @SmallTest
    public void testMakeDepositButton() throws InterruptedException {
        TouchUtils.clickView(this, bt_deposit);

        //waiting for the API
        Thread.sleep(4000);

        //Try if cancel button is onScreen, if true the fragment have started
        final View decorView = clientActivity.getWindow().getDecorView();
        final Button cancel = (Button) clientActivity.findViewById(R.id.bt_cancelTransaction);
        ViewAsserts.assertOnScreen(decorView, cancel);

        //return to the savings fragment
        this.sendKeys(KeyEvent.KEYCODE_BACK);
    }
}
