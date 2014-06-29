package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ClientActivity extends ActionBarActivity implements ClientDetailsFragment.OnFragmentInteractionListener,
        LoanAccountSummaryFragment.OnFragmentInteractionListener,
        LoanRepaymentFragment.OnFragmentInteractionListener,
        SavingsAccountSummaryFragment.OnFragmentInteractionListener {
    /**
     * Static Variables for Inflation of Menu and Submenus
     */

    public static final int MENU_ITEM_SAVE_LOCATION = 1000;
    public static final int MENU_ITEM_DATA_TABLES = 1001;

    //TODO: Ask Vishwas about converting this approach into a HashMap Based approach
    /**
     * Control Menu Changes from Fragments
     * change this Variable to True in the Fragment and Magic
     * Happens in onPrepareOptionsMenu Method Below
     */
    public static Boolean didMenuDataChange = Boolean.FALSE;
    public static Boolean shouldAddDataTables = Boolean.FALSE;
    public static Boolean shouldAddSaveLocation = Boolean.FALSE;
    public static Boolean shouldAddRepaymentSchedule = Boolean.FALSE;
    public static Boolean shouldAddLoanTransactions = Boolean.FALSE;
    /**
     * Property to identify the type of data tables to be shown.
     */
    public static int idOfDataTableToBeShownInMenu = -1;

    /**
     * This list will contain list of data tables
     * and will be used to inflate the Submenu Datatables
     */
    public static List<String> dataTableMenuItems = new ArrayList<String>();

    private int clientId;

    //TODO Try to shorten the code, this activity contains too much of repeated code
    //Implement DRY - Don't Repeat Yourself Approach Here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_container_layout);
        ButterKnife.inject(this);
        clientId = getIntent().getExtras().getInt(Constants.CLIENT_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ClientDetailsFragment clientDetailsFragment = ClientDetailsFragment.newInstance(clientId);
        fragmentTransaction.replace(R.id.global_container, clientDetailsFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }

    /**
     * This method is called EVERY TIME the menu button is pressed
     * on the action bar. So All dynamic changes in the menu are
     * done here.
     *
     * @param menu Current Menu in the Layout
     * @return true if the menu was successfully prepared
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (didMenuDataChange) {
            menu.clear();

            // If the client request fetched data tables this will be true
            if (shouldAddDataTables) {
                // Just another check to make sure the dataTableMenuItems list is not empty
                if (dataTableMenuItems.size() > 0) {
                    /*
                        Now that we have the list, lets add an Option for users to see the sub menu
                        of all data tables available
                     */
                    switch (idOfDataTableToBeShownInMenu) {
                        case Constants.DATA_TABLE_LOANS:
                            menu.addSubMenu(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_LOAN_NAME);
                            break;
                        case Constants.DATA_TABLES_SAVINGS_ACCOUNTS:
                            menu.addSubMenu(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_SAVINGS_ACCOUNTS_NAME);
                            break;
                        default:
                            return false;
                        /*
                            TODO Implement Creation of Data Table Call from here
                            if no data table available
                         */
                    }


                }

                shouldAddDataTables = Boolean.FALSE;
            }

            didMenuDataChange = Boolean.FALSE;
        }



        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    /*
     * Called when a Data table has been selected for viewing
     * from the Sub Menu of Data Table
     *
     * It displays the data of data table based on the name of data table
     */
    public void loadDataTableFragment(int idOfDataTableToBeShownInMenu, int dataTablePostionInTheList) {
        DataTableDataFragment dataTableDataFragment;

        switch (idOfDataTableToBeShownInMenu) {
            case Constants.DATA_TABLE_LOANS:
                dataTableDataFragment =
                        DataTableDataFragment.newInstance(LoanAccountSummaryFragment.loanDataTables.get(dataTablePostionInTheList),
                                LoanAccountSummaryFragment.loanAccountNumber);
                break;
            case Constants.DATA_TABLES_SAVINGS_ACCOUNTS:
                dataTableDataFragment =
                        DataTableDataFragment.newInstance(SavingsAccountSummaryFragment.savingsAccountDataTables.get(dataTablePostionInTheList),
                                SavingsAccountSummaryFragment.savingsAccountNumber);
                break;
            default:
                return;
                        /*
                            TODO Implement Creation of Data Table Call from here
                            if no data table available
                         */
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container, dataTableDataFragment).commit();

    }

    /**
     * This method will perform Fragment Transactions, mostly replace.
     * You create a FragmentTransaction in your fragment and then call this method in this
     * activity.
     * @param fragmentTransaction
     */

    public static void replaceFragment(FragmentTransaction fragmentTransaction) {

        fragmentTransaction.commit();

    }

    /*
     * Called when a Loan Account is Selected
     * from the list of Loan Accounts on Client Details Fragment
     * It displays the summary of the Selected Loan Account
     */

    @Override
    public void loadLoanAccountSummary(int loanAccountNumber) {

        LoanAccountSummaryFragment loanAccountSummaryFragment
                = LoanAccountSummaryFragment.newInstance(loanAccountNumber);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container, loanAccountSummaryFragment).commit();

    }

    /*
     * Called when a Savings Account is Selected
     * from the list of Savings Accounts on Client Details Fragment
     *
     * It displays the summary of the Selected Savings Account
     */

    @Override
    public void loadSavingsAccountSummary(int savingsAccountNumber) {
        SavingsAccountSummaryFragment savingsAccountSummaryFragment
                = SavingsAccountSummaryFragment.newInstance(savingsAccountNumber);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container, savingsAccountSummaryFragment).commit();
    }

    /*
     * Called when the make the make repayment button is clicked
     * in the Loan Account Summary Fragment.
     *
     * It will display the Loan Repayment Fragment where
     * the Information of the repayment has to be filled in.
     */
    @Override
    public void makeRepayment(LoanWithAssociations loan) {

        LoanRepaymentFragment loanRepaymentFragment = LoanRepaymentFragment.newInstance(loan);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.global_container, loanRepaymentFragment).commit();
    }

    /*
     * Called when the Repayment Schedule option from the Menu is
     * clicked
     *
     * It will display the Complete Loan Repayment Schedule.
     */

    @Override
    public void loadRepaymentSchedule(int loanId) {

        LoanRepaymentScheduleFragment loanRepaymentScheduleFragment = LoanRepaymentScheduleFragment.newInstance(loanId);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.global_container, loanRepaymentScheduleFragment).commit();

    }

    /*
     * Called when the Transactions option from the Menu is clicked
     *
     * It will display all the Transactions associated with the Loan
     * and also their details
     */

    @Override
    public void loadLoanTransactions(int loanId) {

        LoanTransactionsFragment loanTransactionsFragment = LoanTransactionsFragment.newInstance(loanId);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.global_container, loanTransactionsFragment).commit();

    }

    /*
     * Called when the make the make deposit button is clicked
     * in the Savings Account Summary Fragment.
     *
     * It will display the Transaction Fragment where the information
     * of the transaction has to be filled in.
     *
     * The transactionType defines if the transaction is a Deposit or a Withdrawal
     *
    */
    @Override
    public void doTransaction(SavingsAccountWithAssociations savingsAccountWithAssociations, String transactionType) {

        SavingsAccountTransactionFragment savingsAccountTransactionFragment =
                SavingsAccountTransactionFragment.newInstance(savingsAccountWithAssociations, transactionType);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.global_container, savingsAccountTransactionFragment).commit();

    }

}
