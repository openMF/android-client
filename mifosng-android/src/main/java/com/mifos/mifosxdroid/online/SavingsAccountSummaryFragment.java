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
    @InjectView(R.id.lv_last_five_savings_transactions) ListView lv_lastFiveTransactions;
    @InjectView(R.id.bt_deposit) Button bt_deposit;
    @InjectView(R.id.bt_withdrawal) Button bt_withdrawal;

    private OnFragmentInteractionListener mListener;

    public static int savingsAccountNumber;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    SavingsAccountWithAssociations savingsAccountWithAssociations;

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

                            SavingsAccountTransactionsListAdapter savingsAccountTransactionsListAdapter
                                    = new SavingsAccountTransactionsListAdapter(getActivity().getApplicationContext(),
                                    savingsAccountWithAssociations.getTransactions().size()<5?
                                            savingsAccountWithAssociations.getTransactions():
                                            savingsAccountWithAssociations.getTransactions().subList(0,5));
                            lv_lastFiveTransactions.setAdapter(savingsAccountTransactionsListAdapter);

                            safeUIBlockingUtility.safelyUnBlockUI();

                            inflateDataTablesList();
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


    public static List<DataTable> savingsAccountDataTables = new ArrayList<DataTable>();


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

}
