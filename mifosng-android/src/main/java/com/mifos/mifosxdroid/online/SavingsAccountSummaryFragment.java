package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SavingsAccountTransactionsListAdapter;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.accounts.savings.Transaction;
import com.mifos.objects.noncore.DataTable;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SavingsAccountSummaryFragment extends Fragment {

    @InjectView(R.id.tv_clientName) TextView tv_clientName;
    @InjectView(R.id.quickContactBadge_client) QuickContactBadge quickContactBadge;
    @InjectView(R.id.tv_savings_product_short_name) TextView tv_savingsProductName;
    @InjectView(R.id.tv_savingsAccountNumber) TextView tv_savingsAccountNumber;
    @InjectView(R.id.tv_savings_account_balance) TextView tv_savingsAccountBalance;
    @InjectView(R.id.tv_total_deposits) TextView tv_totalDeposits;
    @InjectView(R.id.tv_total_withdrawals) TextView tv_totalWithdrawals;
    @InjectView(R.id.lv_savings_transactions) ListView lv_Transactions;
    @InjectView(R.id.bt_deposit) Button bt_deposit;
    @InjectView(R.id.bt_withdrawal) Button bt_withdrawal;

    private OnFragmentInteractionListener mListener;

    public static int savingsAccountNumber;

    public static List<DataTable> savingsAccountDataTables = new ArrayList<DataTable>();

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    SavingsAccountWithAssociations savingsAccountWithAssociations;


    // Cached List of all savings account transactions
    // that are used for inflation of rows in
    // Infinite Scroll View
    List<Transaction> listOfAllTransactions = new ArrayList<Transaction>();
    int countOfTransactionsInListView = 0;
    SavingsAccountTransactionsListAdapter savingsAccountTransactionsListAdapter;

    boolean LOADMORE; // variable to enable and disable loading of data into listview

    // variables to capture position of first visible items
    // so that while loading the listview does not scroll automatically
    int index,top;

    // variables to control amount of data loading on each load
    int INITIAL = 0;
    int FINAL = 5;

    public static SavingsAccountSummaryFragment newInstance(int savingsAccountNumber) {
        SavingsAccountSummaryFragment fragment = new SavingsAccountSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public SavingsAccountSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_savings_account_summary, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(SavingsAccountSummaryFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);

        inflateSavingsAccountSummary();

        return rootView;
    }

    public void inflateSavingsAccountSummary(){

        safeUIBlockingUtility.safelyBlockUI();

        actionBar.setTitle(getResources().getString(R.string.savingsAccountSummary));
        /**
         * This Method will hit end point ?associations=transactions
         */
        API.savingsAccountService.getSavingsAccountWithAssociations(savingsAccountNumber,
                "transactions", new Callback<SavingsAccountWithAssociations>() {
                    @Override
                    public void success(SavingsAccountWithAssociations savingsAccountWithAssociations, Response response) {

                        if(savingsAccountWithAssociations!=null) {

                            SavingsAccountSummaryFragment.this.savingsAccountWithAssociations = savingsAccountWithAssociations;

                            tv_clientName.setText(savingsAccountWithAssociations.getClientName());
                            tv_savingsProductName.setText(savingsAccountWithAssociations.getSavingsProductName());
                            tv_savingsAccountNumber.setText(savingsAccountWithAssociations.getAccountNo());
                            tv_savingsAccountBalance.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getAccountBalance()));
                            tv_totalDeposits.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getTotalDeposits()));
                            tv_totalWithdrawals.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getTotalWithdrawals()));

                            savingsAccountTransactionsListAdapter
                                    = new SavingsAccountTransactionsListAdapter(getActivity().getApplicationContext(),
                                    savingsAccountWithAssociations.getTransactions().size()< FINAL ?
                                            savingsAccountWithAssociations.getTransactions():
                                            savingsAccountWithAssociations.getTransactions().subList(INITIAL,FINAL));
                            lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);

                            // Cache transactions here
                            listOfAllTransactions.addAll(savingsAccountWithAssociations.getTransactions());

                            updateMenu();

                            safeUIBlockingUtility.safelyUnBlockUI();

                            inflateDataTablesList();

                            enableInfiniteScrollOfTransactions();

                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                        Log.i(getActivity().getLocalClassName(), retrofitError.getLocalizedMessage());

                        Toast.makeText(activity, "Savings Account not found.", Toast.LENGTH_SHORT).show();
                        safeUIBlockingUtility.safelyUnBlockUI();
                    }
                });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @OnClick(R.id.bt_deposit)
    public void onDepositButtonClicked() {
        mListener.doTransaction(savingsAccountWithAssociations, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT);
    }

    @OnClick(R.id.bt_withdrawal)
    public void onWithdrawalButtonClicked() {
        mListener.doTransaction(savingsAccountWithAssociations, Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL);
    }

    public interface OnFragmentInteractionListener {

        public void doTransaction(SavingsAccountWithAssociations savingsAccountWithAssociations, String transactionType);
    }

    /**
     * Use this method to fetch all datatables for a savings account and inflate them as
     * menu options
     */
    public void inflateDataTablesList(){

        safeUIBlockingUtility.safelyBlockUI();

        //TODO change loan service to savings account service
        API.dataTableService.getDatatablesOfSavingsAccount(new Callback<List<DataTable>>() {
            @Override
            public void success(List<DataTable> dataTables, Response response) {

                if (dataTables != null) {
                    ClientActivity.idOfDataTableToBeShownInMenu = Constants.DATA_TABLES_SAVINGS_ACCOUNTS;
                    ClientActivity.shouldAddDataTables = Boolean.TRUE;
                    ClientActivity.didMenuDataChange = Boolean.TRUE;
                    Iterator<DataTable> dataTableIterator = dataTables.iterator();
                    ClientActivity.dataTableMenuItems.clear();
                    while (dataTableIterator.hasNext()) {
                        DataTable dataTable = dataTableIterator.next();
                        savingsAccountDataTables.add(dataTable);
                        ClientActivity.dataTableMenuItems.add(dataTable.getRegisteredTableName());
                    }
                }

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i("DATATABLE", retrofitError.getLocalizedMessage());
                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });

    }

    public void enableInfiniteScrollOfTransactions() {


        lv_Transactions.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE) {
                    LOADMORE = false;
                } else {
                    LOADMORE = true;
                }


            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                final int lastItem = firstVisibleItem + visibleItemCount;

                if (firstVisibleItem == 0) {
                    return;
                }

                if (lastItem == totalItemCount && LOADMORE) {

                    LOADMORE = false;

                    loadNextFiveTransactions();

                }
            }

        });


    }

    public void loadNextFiveTransactions() {

        index = lv_Transactions.getFirstVisiblePosition();

        View v = lv_Transactions.getChildAt(0);

        top = (v == null)? 0 : v.getTop();

        FINAL += 5;

        if(FINAL > listOfAllTransactions.size())
        {
            FINAL = listOfAllTransactions.size();
            savingsAccountTransactionsListAdapter =
                    new SavingsAccountTransactionsListAdapter(getActivity(),
                            listOfAllTransactions.subList(INITIAL, FINAL));
            savingsAccountTransactionsListAdapter.notifyDataSetChanged();
            lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);
            lv_Transactions.setSelectionFromTop(index, top);
            return;
        }

        savingsAccountTransactionsListAdapter =
                new SavingsAccountTransactionsListAdapter(getActivity(),
                        listOfAllTransactions.subList(INITIAL, FINAL));
        savingsAccountTransactionsListAdapter.notifyDataSetChanged();
        lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);
        lv_Transactions.setSelectionFromTop(index, top);


    }

    public void updateMenu() {

        ClientActivity.shouldAddRepaymentSchedule = Boolean.FALSE;
        ClientActivity.shouldAddSaveLocation = Boolean.FALSE;
        ClientActivity.didMenuDataChange = Boolean.TRUE;

    }

}
