/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingsaccountsummaryfragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SavingsAccountTransactionsListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.datatabledatafragment.DataTableDataFragment;
import com.mifos.mifosxdroid.online.documentlistfragment.DocumentListFragment;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.accounts.savings.Status;
import com.mifos.objects.accounts.savings.Transaction;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SavingsAccountSummaryFragment extends MifosBaseFragment implements SavingsAccountSummaryMvpView{

    public static final int MENU_ITEM_SEARCH = 2000;
    public static final int MENU_ITEM_DATA_TABLES = 1001;
    public static final int MENU_ITEM_DOCUMENTS = 1004;
    public static int savingsAccountNumber;
    public static DepositType savingsAccountType;

    public static List<DataTable> savingsAccountDataTables = new ArrayList<DataTable>();
    @InjectView(R.id.tv_clientName)
    TextView tv_clientName;
    @InjectView(R.id.quickContactBadge_client)
    QuickContactBadge quickContactBadge;
    @InjectView(R.id.tv_savings_product_short_name)
    TextView tv_savingsProductName;
    @InjectView(R.id.tv_savingsAccountNumber)
    TextView tv_savingsAccountNumber;
    @InjectView(R.id.tv_savings_account_balance)
    TextView tv_savingsAccountBalance;
    @InjectView(R.id.tv_total_deposits)
    TextView tv_totalDeposits;
    @InjectView(R.id.tv_total_withdrawals)
    TextView tv_totalWithdrawals;
    @InjectView(R.id.lv_savings_transactions)
    ListView lv_Transactions;
    @InjectView(R.id.tv_interest_earned)
    TextView tv_interestEarned;
    @InjectView(R.id.bt_deposit)
    Button bt_deposit;
    @InjectView(R.id.bt_withdrawal)
    Button bt_withdrawal;
    private View rootView;

    private SavingsAccountWithAssociations savingsAccountWithAssociations;
    // Cached List of all savings account transactions
    // that are used for inflation of rows in
    // Infinite Scroll View
    List<Transaction> listOfAllTransactions = new ArrayList<Transaction>();
    int countOfTransactionsInListView = 0;
    SavingsAccountTransactionsListAdapter savingsAccountTransactionsListAdapter;
    private boolean LOADMORE; // variable to enable and disable loading of data into listview
    // variables to capture position of first visible items
    // so that while loading the listview does not scroll automatically
    private int index, top;
    // variables to control amount of data loading on each load
    private int INITIAL = 0;
    private int FINAL = 5;
    private OnFragmentInteractionListener mListener;
    private DataManager dataManager;
    private SavingsAccountSummaryPresenter mSavingsAccountSummaryPresenter;

    public static SavingsAccountSummaryFragment newInstance(int savingsAccountNumber, DepositType type) {
        SavingsAccountSummaryFragment fragment = new SavingsAccountSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber);
        args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER);
            savingsAccountType = getArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_savings_account_summary, container, false);
        ButterKnife.inject(this, rootView);
        dataManager =new DataManager();
        mSavingsAccountSummaryPresenter = new SavingsAccountSummaryPresenter(dataManager);
        mSavingsAccountSummaryPresenter.attachView(this);
        inflateSavingsAccountSummary();
        return rootView;
    }

    public void inflateSavingsAccountSummary() {
        hideProgress();
        switch (savingsAccountType.getServerType()) {
            case RECURRING:
                setToolbarTitle(getResources().getString(R.string.recurringAccountSummary));
                break;
            default:
                setToolbarTitle(getResources().getString(R.string.savingsAccountSummary));
                break;
        }
        /**
         * This Method will hit end point ?associations=transactions
         */
        mSavingsAccountSummaryPresenter.loadSavingAccount(savingsAccountType.getEndpoint(), savingsAccountNumber, "transactions");

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem mItemSearch = menu.add(Menu.NONE, MENU_ITEM_SEARCH, Menu.NONE, getString(R.string.search));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mItemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.addSubMenu(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_SAVINGS_ACCOUNTS_NAME);
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getResources().getString(R.string.documents));

        // This is the ID of Each data table which will be used in onOptionsItemSelected Method
        int SUBMENU_ITEM_ID = 0;
        // Create a Sub Menu that holds a link to all data tables
        SubMenu dataTableSubMenu = menu.getItem(1).getSubMenu();
        if (dataTableSubMenu != null && savingsAccountDataTables != null && savingsAccountDataTables.size() > 0) {
            Iterator<DataTable> dataTableIterator = savingsAccountDataTables.iterator();
            while (dataTableIterator.hasNext()) {
                dataTableSubMenu.add(Menu.NONE, SUBMENU_ITEM_ID, Menu.NONE, dataTableIterator.next().getRegisteredTableName());
                SUBMENU_ITEM_ID++;
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id >= 0 && id < savingsAccountDataTables.size()) {
            DataTableDataFragment dataTableDataFragment = DataTableDataFragment.newInstance(savingsAccountDataTables.get(id), savingsAccountNumber);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
            fragmentTransaction.replace(R.id.container, dataTableDataFragment);
            fragmentTransaction.commit();
        }

        if (item.getItemId() == MENU_ITEM_DOCUMENTS)
            loadDocuments();

        else if (id == MENU_ITEM_SEARCH)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_deposit)
    public void onDepositButtonClicked() {
        mListener.doTransaction(savingsAccountWithAssociations, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT, savingsAccountType);
    }

    @OnClick(R.id.bt_withdrawal)
    public void onWithdrawalButtonClicked() {
        mListener.doTransaction(savingsAccountWithAssociations, Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL, savingsAccountType);
    }

    /**
     * Use this method to fetch all datatables for a savings account and inflate them as
     * menu options
     */
    public void inflateDataTablesList() {
        showProgress();
        //TODO change loan service to savings account service
        mSavingsAccountSummaryPresenter.loadSavingsDataTable();
    }

    public void enableInfiniteScrollOfTransactions() {
        lv_Transactions.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                LOADMORE = !(scrollState == SCROLL_STATE_IDLE);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if (firstVisibleItem == 0)
                    return;

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
        top = (v == null) ? 0 : v.getTop();
        FINAL += 5;
        if (FINAL > listOfAllTransactions.size()) {
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

    public void loadDocuments() {
        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_SAVINGS, savingsAccountNumber);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, documentListFragment);
        fragmentTransaction.commit();
    }

    public void toggleTransactionCapabilityOfAccount(Status status) {
        if (!status.getActive()) {
            bt_deposit.setVisibility(View.GONE);
            bt_withdrawal.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSavingAccount(SavingsAccountWithAssociations savingsAccountWithAssociations) {
        if (savingsAccountWithAssociations != null) {

            SavingsAccountSummaryFragment.this.savingsAccountWithAssociations = savingsAccountWithAssociations;

            tv_clientName.setText(savingsAccountWithAssociations.getClientName());
            tv_savingsProductName.setText(savingsAccountWithAssociations.getSavingsProductName());
            tv_savingsAccountNumber.setText(savingsAccountWithAssociations.getAccountNo());

            if (savingsAccountWithAssociations.getSummary().getTotalInterestEarned() != null) {
                tv_interestEarned.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getTotalInterestEarned()));
            } else {
                tv_interestEarned.setText("0.0");
            }

            tv_savingsAccountBalance.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getAccountBalance()));

            if (savingsAccountWithAssociations.getSummary().getTotalDeposits() != null) {
                tv_totalDeposits.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getTotalDeposits()));
            } else {
                tv_totalDeposits.setText("0.0");
            }

            if (savingsAccountWithAssociations.getSummary().getTotalWithdrawals() != null) {
                tv_totalWithdrawals.setText(String.valueOf(savingsAccountWithAssociations.getSummary().getTotalWithdrawals()));
            } else {
                tv_totalWithdrawals.setText("0.0");
            }

            savingsAccountTransactionsListAdapter
                    = new SavingsAccountTransactionsListAdapter(getActivity().getApplicationContext(),
                    savingsAccountWithAssociations.getTransactions().size() < FINAL ?
                            savingsAccountWithAssociations.getTransactions() :
                            savingsAccountWithAssociations.getTransactions().subList(INITIAL, FINAL)
            );
            lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);

            // Cache transactions here
            listOfAllTransactions.addAll(savingsAccountWithAssociations.getTransactions());

            lv_Transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    /*
                                     On Click at a Savings Account Transaction
                                     1. get the transactionId of that transaction
                                     2. get the account Balance after that transaction
                                    */
                    int transactionId = listOfAllTransactions.get(i).getId();
                    double runningBalance = listOfAllTransactions.get(i).getRunningBalance();

                    //Display them as a Formatted string in a toast message
                    Toast.makeText(getActivity(),
                            String.format(getResources().getString(R.string.savings_transaction_detail),
                                    transactionId, runningBalance),
                            Toast.LENGTH_LONG
                    ).show();

                }
            });

            toggleTransactionCapabilityOfAccount(savingsAccountWithAssociations.getStatus());

            inflateDataTablesList();
            hideProgress();
            enableInfiniteScrollOfTransactions();
        }
    }

    @Override
    public void ResponseFailedSavingAccount(String s) {
        Toaster.show(rootView, s);
        hideProgress();
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void showSavingDataTable(List<DataTable> dataTables) {
        if (dataTables != null) {
            Iterator<DataTable> dataTableIterator = dataTables.iterator();
            savingsAccountDataTables.clear();
            while (dataTableIterator.hasNext()) {
                DataTable dataTable = dataTableIterator.next();
                savingsAccountDataTables.add(dataTable);
            }
        }
        hideProgress();
    }

    @Override
    public void ResponseErrorLoading(String s) {
        Toaster.show(rootView,s);
        hideProgress();
    }

    public interface OnFragmentInteractionListener {
        void doTransaction(SavingsAccountWithAssociations savingsAccountWithAssociations, String transactionType, DepositType accountType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSavingsAccountSummaryPresenter.detachView();
    }
}
