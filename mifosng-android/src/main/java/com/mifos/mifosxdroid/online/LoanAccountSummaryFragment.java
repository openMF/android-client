/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.LoanAccountApproval;
import com.mifos.mifosxdroid.dialogfragments.LoanAccountDisbursement;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Charges;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 09/05/14.
 */
public class LoanAccountSummaryFragment extends ProgressableFragment {


    public static final int MENU_ITEM_DATA_TABLES = 1001;
    public static final int MENU_ITEM_REPAYMENT_SCHEDULE = 1002;
    public static final int MENU_ITEM_LOAN_TRANSACTIONS = 1003;
    public static final int MENU_ITEM_DOCUMENTS = 1004;
    public static final int MENU_ITEM_CHARGES = 1005;
    /*
        Set of Actions and Transactions that can be performed depending on the status of the Loan
        Actions are performed to change the status of the loan
        Transactions are performed to do repayments
     */
    private static final int ACTION_NOT_SET = -1;
    private static final int ACTION_APPROVE_LOAN = 0;
    private static final int ACTION_DISBURSE_LOAN = 1;
    private static final int TRANSACTION_REPAYMENT = 2;
    public static int loanAccountNumber;
    public static List<DataTable> loanDataTables = new ArrayList<DataTable>();
    @InjectView(R.id.view_status_indicator)
    View view_status_indicator;
    @InjectView(R.id.tv_clientName)
    TextView tv_clientName;
    @InjectView(R.id.quickContactBadge_client)
    QuickContactBadge quickContactBadge;
    @InjectView(R.id.tv_loan_product_short_name)
    TextView tv_loan_product_short_name;
    @InjectView(R.id.tv_loanAccountNumber)
    TextView tv_loanAccountNumber;
    @InjectView(R.id.tv_amount_disbursed)
    TextView tv_amount_disbursed;
    @InjectView(R.id.tv_disbursement_date)
    TextView tv_disbursement_date;
    @InjectView(R.id.tv_in_arrears)
    TextView tv_in_arrears;
    @InjectView(R.id.tv_loan_officer)
    TextView tv_loan_officer;
    @InjectView(R.id.tv_principal)
    TextView tv_principal;
    @InjectView(R.id.tv_loan_principal_due)
    TextView tv_loan_principal_due;
    @InjectView(R.id.tv_loan_principal_paid)
    TextView tv_loan_principal_paid;
    @InjectView(R.id.tv_interest)
    TextView tv_interest;
    @InjectView(R.id.tv_loan_interest_due)
    TextView tv_loan_interest_due;
    @InjectView(R.id.tv_loan_interest_paid)
    TextView tv_loan_interest_paid;
    @InjectView(R.id.tv_fees)
    TextView tv_fees;
    @InjectView(R.id.tv_loan_fees_due)
    TextView tv_loan_fees_due;
    @InjectView(R.id.tv_loan_fees_paid)
    TextView tv_loan_fees_paid;
    @InjectView(R.id.tv_penalty)
    TextView tv_penalty;
    @InjectView(R.id.tv_loan_penalty_due)
    TextView tv_loan_penalty_due;
    @InjectView(R.id.tv_loan_penalty_paid)
    TextView tv_loan_penalty_paid;
    @InjectView(R.id.tv_total)
    TextView tv_total;
    @InjectView(R.id.tv_total_due)
    TextView tv_total_due;
    @InjectView(R.id.tv_total_paid)
    TextView tv_total_paid;
    @InjectView(R.id.bt_processLoanTransaction)
    Button bt_processLoanTransaction;
    List<Charges> chargesList = new ArrayList<Charges>();
    private View rootView;
    // Action Identifier in the onProcessTransactionClicked Method
    private int processLoanTransactionAction = -1;
    private OnFragmentInteractionListener mListener;
    private LoanWithAssociations clientLoanWithAssociations;

    public LoanAccountSummaryFragment() {
        // Required empty public constructor
    }

    public static LoanAccountSummaryFragment newInstance(int loanAccountNumber) {
        LoanAccountSummaryFragment fragment = new LoanAccountSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        //Necessary Call to add and update the Menu in a Fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_account_summary, container, false);
        ButterKnife.inject(this, rootView);
        inflateLoanAccountSummary();
        return rootView;
    }

    private void inflateLoanAccountSummary() {
        showProgress(true);
        setToolbarTitle(getResources().getString(R.string.loanAccountSummary));
        //TODO Implement cases to enable/disable repayment button
        bt_processLoanTransaction.setEnabled(false);

        App.apiManager.getLoanById(loanAccountNumber, new Callback<LoanWithAssociations>() {

            @Override
            public void success(LoanWithAssociations loanWithAssociations, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;

                clientLoanWithAssociations = loanWithAssociations;
                tv_clientName.setText(loanWithAssociations.getClientName());
                tv_loan_product_short_name.setText(loanWithAssociations.getLoanProductName());
                tv_loanAccountNumber.setText("#" + loanWithAssociations.getAccountNo());
                tv_loan_officer.setText(loanWithAssociations.getLoanOfficerName());
                //TODO Implement QuickContactBadge
                //quickContactBadge.setImageToDefault();

                bt_processLoanTransaction.setEnabled(true);
                if (loanWithAssociations.getStatus().getActive()) {
                    inflateLoanSummary(loanWithAssociations);
                    // if Loan is already active
                    // the Transaction Would be Make Repayment
                    view_status_indicator.setBackgroundColor(getResources().getColor(R.color.light_green));
                    bt_processLoanTransaction.setText("Make Repayment");
                    processLoanTransactionAction = TRANSACTION_REPAYMENT;

                } else if (loanWithAssociations.getStatus().getPendingApproval()) {
                    // if Loan is Pending for Approval
                    // the Action would be Approve Loan
                    view_status_indicator.setBackgroundColor(getResources().getColor(R.color.blue));
                    bt_processLoanTransaction.setText("Approve Loan");
                    processLoanTransactionAction = ACTION_APPROVE_LOAN;
                } else if (loanWithAssociations.getStatus().getWaitingForDisbursal()) {
                    // if Loan is Waiting for Disbursal
                    // the Action would be Disburse Loan
                    view_status_indicator.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                    bt_processLoanTransaction.setText("Disburse Loan");
                    processLoanTransactionAction = ACTION_DISBURSE_LOAN;
                } else if (loanWithAssociations.getStatus().getClosedObligationsMet()) {
                    inflateLoanSummary(loanWithAssociations);
                    // if Loan is Closed after the obligations are met
                    // the make payment will be disabled so that no more payment can be collected
                    view_status_indicator.setBackgroundColor(getResources().getColor(R.color.black));
                    bt_processLoanTransaction.setEnabled(false);
                    bt_processLoanTransaction.setText("Make Repayment");
                } else {
                    inflateLoanSummary(loanWithAssociations);
                    view_status_indicator.setBackgroundColor(getResources().getColor(R.color.black));
                    bt_processLoanTransaction.setEnabled(false);
                    bt_processLoanTransaction.setText("Loan Closed");
                }
                showProgress(false);
                inflateDataTablesList();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toaster.show(rootView, "Loan Account not found.");
                showProgress(false);
            }
        });

    }

    @OnClick(R.id.bt_processLoanTransaction)
    public void onProcessTransactionClicked() {
        if (processLoanTransactionAction == TRANSACTION_REPAYMENT) {
            mListener.makeRepayment(clientLoanWithAssociations);
        } else if (processLoanTransactionAction == ACTION_APPROVE_LOAN) {
            approveLoan();
        } else if (processLoanTransactionAction == ACTION_DISBURSE_LOAN) {
            disburseLoan();
        } else {
            Log.i(getActivity().getLocalClassName(), "TRANSACTION ACTION NOT SET");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.addSubMenu(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_LOAN_NAME);
        menu.add(Menu.NONE, MENU_ITEM_LOAN_TRANSACTIONS, Menu.NONE, getResources().getString(R.string.transactions));
        menu.add(Menu.NONE, MENU_ITEM_REPAYMENT_SCHEDULE, Menu.NONE, getResources().getString(R.string.loan_repayment_schedule));
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getResources().getString(R.string.documents));
        menu.add(Menu.NONE, MENU_ITEM_CHARGES, Menu.NONE, getResources().getString(R.string.charges));

        int SUBMENU_ITEM_ID = 0;

        // Create a Sub Menu that holds a link to all data tables
        SubMenu dataTableSubMenu = menu.getItem(1).getSubMenu();
        if (dataTableSubMenu != null && loanDataTables != null && loanDataTables.size() > 0) {
            Iterator<DataTable> dataTableIterator = loanDataTables.iterator();
            while (dataTableIterator.hasNext()) {
                dataTableSubMenu.add(Menu.NONE, SUBMENU_ITEM_ID, Menu.NONE, dataTableIterator.next().getRegisteredTableName());
                SUBMENU_ITEM_ID++;
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ITEM_REPAYMENT_SCHEDULE)
            mListener.loadRepaymentSchedule(loanAccountNumber);

        if (item.getItemId() == MENU_ITEM_LOAN_TRANSACTIONS)
            mListener.loadLoanTransactions(loanAccountNumber);

        if (item.getItemId() >= 0 && item.getItemId() < loanDataTables.size()) {
            DataTableDataFragment dataTableDataFragment = DataTableDataFragment.newInstance(loanDataTables.get(item.getItemId()), loanAccountNumber);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
            fragmentTransaction.replace(R.id.global_container, dataTableDataFragment);
            fragmentTransaction.commit();
        }

        if (item.getItemId() == MENU_ITEM_DOCUMENTS) {
            loadDocuments();
        }
        if (item.getItemId() == MENU_ITEM_CHARGES) {
            loadloanCharges();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Use this method to fetch all datatables for client and inflate them as
     * menu options
     */
    public void inflateDataTablesList() {
        showProgress(true);
        App.apiManager.getLoanDataTable(new Callback<List<DataTable>>() {
            @Override
            public void success(List<DataTable> dataTables, Response response) {
                if (dataTables != null) {
                    Iterator<DataTable> dataTableIterator = dataTables.iterator();
                    loanDataTables.clear();
                    while (dataTableIterator.hasNext()) {
                        DataTable dataTable = dataTableIterator.next();
                        loanDataTables.add(dataTable);
                    }
                }
                showProgress(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
            }
        });
    }

    public void inflateLoanSummary(LoanWithAssociations loanWithAssociations) {
        tv_amount_disbursed.setText(String.valueOf(loanWithAssociations.getSummary().getPrincipalDisbursed()));
        try {
            tv_disbursement_date.setText(DateHelper.getDateAsString(loanWithAssociations.getTimeline().getActualDisbursementDate()));
        } catch (IndexOutOfBoundsException exception) {
            Toast.makeText(getActivity(), getResources().getString(R.string.loan_rejected_message), Toast.LENGTH_SHORT).show();
        }
        tv_in_arrears.setText(String.valueOf(loanWithAssociations.getSummary().getTotalOverdue()));
        tv_principal.setText(String.valueOf(loanWithAssociations.getSummary().getPrincipalDisbursed()));
        tv_loan_principal_due.setText(String.valueOf(loanWithAssociations.getSummary().getPrincipalOutstanding()));
        tv_loan_principal_paid.setText(String.valueOf(loanWithAssociations.getSummary().getPrincipalPaid()));

        tv_interest.setText(String.valueOf(loanWithAssociations.getSummary().getInterestCharged()));
        tv_loan_interest_due.setText(String.valueOf(loanWithAssociations.getSummary().getInterestOutstanding()));
        tv_loan_interest_paid.setText(String.valueOf(loanWithAssociations.getSummary().getInterestPaid()));

        tv_fees.setText(String.valueOf(loanWithAssociations.getSummary().getFeeChargesCharged()));
        tv_loan_fees_due.setText(String.valueOf(loanWithAssociations.getSummary().getFeeChargesOutstanding()));
        tv_loan_fees_paid.setText(String.valueOf(loanWithAssociations.getSummary().getFeeChargesPaid()));

        tv_penalty.setText(String.valueOf(loanWithAssociations.getSummary().getPenaltyChargesCharged()));
        tv_loan_penalty_due.setText(String.valueOf(loanWithAssociations.getSummary().getPenaltyChargesOutstanding()));
        tv_loan_penalty_paid.setText(String.valueOf(loanWithAssociations.getSummary().getPenaltyChargesPaid()));

        tv_total.setText(String.valueOf(loanWithAssociations.getSummary().getTotalExpectedRepayment()));
        tv_total_due.setText(String.valueOf(loanWithAssociations.getSummary().getTotalOutstanding()));
        tv_total_paid.setText(String.valueOf(loanWithAssociations.getSummary().getTotalRepayment()));
    }

    public void loadDocuments() {
        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_LOANS, loanAccountNumber);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, documentListFragment);
        fragmentTransaction.commit();
    }

    public void loadloanCharges() {

        LoanChargeFragment loanChargeFragment = LoanChargeFragment.newInstance(loanAccountNumber, chargesList);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, loanChargeFragment);
        fragmentTransaction.commit();
    }

    public void approveLoan() {

        LoanAccountApproval loanAccountApproval = LoanAccountApproval.newInstance(loanAccountNumber);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, loanAccountApproval);
        fragmentTransaction.commit();
    }

    public void disburseLoan() {

        LoanAccountDisbursement loanAccountDisbursement = LoanAccountDisbursement.newInstance(loanAccountNumber);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, loanAccountDisbursement);
        fragmentTransaction.commit();

    }

    public interface OnFragmentInteractionListener {
        void makeRepayment(LoanWithAssociations loan);

        void loadRepaymentSchedule(int loanId);

        void loadLoanTransactions(int loanId);
    }
}
