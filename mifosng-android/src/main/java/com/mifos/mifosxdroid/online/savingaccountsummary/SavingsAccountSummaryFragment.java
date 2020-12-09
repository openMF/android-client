/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingaccountsummary;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SavingsAccountTransactionsListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.online.datatable.DataTableFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.online.savingsaccountactivate.SavingsAccountActivateFragment;
import com.mifos.mifosxdroid.online.savingsaccountapproval.SavingsAccountApprovalFragment;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.accounts.savings.Status;
import com.mifos.objects.accounts.savings.Transaction;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SavingsAccountSummaryFragment extends ProgressableFragment
        implements SavingsAccountSummaryMvpView {

    private static final int SAVE_AS_PDF = 1005;
    public static final int MENU_ITEM_DATA_TABLES = 1001;
    public static final int MENU_ITEM_DOCUMENTS = 1004;
    private static final int ACTION_APPROVE_SAVINGS = 4;
    private static final int ACTION_ACTIVATE_SAVINGS = 5;

    private String directory = Environment.getExternalStorageDirectory().getPath() +
            File.separator + "Mifos Docs";
    public int savingsAccountNumber;
    public DepositType savingsAccountType;

    @BindView(R.id.tv_clientName)
    TextView tv_clientName;

    @BindView(R.id.quickContactBadge_client)
    QuickContactBadge quickContactBadge;

    @BindView(R.id.tv_savings_product_short_name)
    TextView tv_savingsProductName;

    @BindView(R.id.tv_savingsAccountNumber)
    TextView tv_savingsAccountNumber;

    @BindView(R.id.tv_savings_account_balance)
    TextView tv_savingsAccountBalance;

    @BindView(R.id.tv_total_deposits)
    TextView tv_totalDeposits;

    @BindView(R.id.tv_total_withdrawals)
    TextView tv_totalWithdrawals;

    @BindView(R.id.lv_savings_transactions)
    ListView lv_Transactions;

    @BindView(R.id.tv_interest_earned)
    TextView tv_interestEarned;

    @BindView(R.id.bt_deposit)
    Button bt_deposit;

    @BindView(R.id.bt_withdrawal)
    Button bt_withdrawal;

    @BindView(R.id.bt_approve_saving)
    Button bt_approve_saving;

    @Inject
    SavingsAccountSummaryPresenter mSavingAccountSummaryPresenter;

    // Cached List of all savings account transactions
    // that are used for inflation of rows in
    // Infinite Scroll View
    List<Transaction> listOfAllTransactions = new ArrayList<Transaction>();
    SavingsAccountTransactionsListAdapter savingsAccountTransactionsListAdapter;
    private View rootView;
    private int processSavingTransactionAction = -1;
    private SavingsAccountWithAssociations savingsAccountWithAssociations;
    private boolean parentFragment = true;
    private boolean loadmore; // variable to enable and disable loading of data into listview
    // variables to capture position of first visible items
    // so that while loading the listview does not scroll automatically
    private int index, top;
    // variables to control amount of data loading on each load
    private int initial = 0;
    private int last = 5;
    private OnFragmentInteractionListener mListener;

    public static SavingsAccountSummaryFragment newInstance(int savingsAccountNumber,
                                                            DepositType type,
                                                            boolean parentFragment) {
        SavingsAccountSummaryFragment fragment = new SavingsAccountSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber);
        args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type);
        args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, parentFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER);
            savingsAccountType = getArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE);
            parentFragment = getArguments().getBoolean(Constants.IS_A_PARENT_FRAGMENT);
        }
        inflateSavingsAccountSummary();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_savings_account_summary, container, false);

        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, rootView);
        mSavingAccountSummaryPresenter.attachView(this);

        mSavingAccountSummaryPresenter
                .loadSavingAccount(savingsAccountType.getEndpoint(), savingsAccountNumber);
        return rootView;
    }

    /**
     * This Method setting the ToolBar Title and Requesting the API for Saving Account
     */
    public void inflateSavingsAccountSummary() {
        showProgress(true);
        switch (savingsAccountType.getServerType()) {
            case RECURRING:
                setToolbarTitle(getResources().getString(R.string.recurringAccountSummary));
                break;
            default:
                setToolbarTitle(getResources().getString(R.string.savingsAccountSummary));
                break;
        }
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
        if (!parentFragment) {
            getActivity().finish();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants
                .DATA_TABLE_SAVINGS_ACCOUNTS_NAME);
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE,
                getResources().getString(R.string.documents));
        menu.add(Menu.NONE, SAVE_AS_PDF, Menu.NONE, Constants.SAVE_AS_PDF);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_DOCUMENTS) {
            loadDocuments();
        } else if (id == MENU_ITEM_DATA_TABLES) {
            loadSavingsDataTables();
        } else if (id == SAVE_AS_PDF) {
            if (checkPermission()) {
                createPDF();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_deposit)
    public void onDepositButtonClicked() {
        mListener.doTransaction(savingsAccountWithAssociations,
                Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT, savingsAccountType);
    }

    @OnClick(R.id.bt_withdrawal)
    public void onWithdrawalButtonClicked() {
        mListener.doTransaction(savingsAccountWithAssociations,
                Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL, savingsAccountType);
    }

    @OnClick(R.id.bt_approve_saving)
    public void onProcessTransactionClicked() {
        if (processSavingTransactionAction == ACTION_APPROVE_SAVINGS) {
            approveSavings();
        } else if (processSavingTransactionAction == ACTION_ACTIVATE_SAVINGS) {
            activateSavings();
        } else {
            Log.i(getActivity().getLocalClassName(),
                    getResources().getString(R.string.transaction_action_not_set));
        }
    }

    public void enableInfiniteScrollOfTransactions() {
        lv_Transactions.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                loadmore = !(scrollState == SCROLL_STATE_IDLE);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int
                    visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if (firstVisibleItem == 0) {
                    return;
                }

                if (lastItem == totalItemCount && loadmore) {
                    loadmore = false;
                    loadNextFiveTransactions();
                }
            }
        });
    }

    public void loadNextFiveTransactions() {
        index = lv_Transactions.getFirstVisiblePosition();
        View v = lv_Transactions.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        last += 5;
        if (last > listOfAllTransactions.size()) {
            last = listOfAllTransactions.size();
            savingsAccountTransactionsListAdapter =
                    new SavingsAccountTransactionsListAdapter(getActivity(),
                            listOfAllTransactions.subList(initial, last));
            savingsAccountTransactionsListAdapter.notifyDataSetChanged();
            lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);
            lv_Transactions.setSelectionFromTop(index, top);
            return;
        }

        savingsAccountTransactionsListAdapter =
                new SavingsAccountTransactionsListAdapter(getActivity(),
                        listOfAllTransactions.subList(initial, last));
        savingsAccountTransactionsListAdapter.notifyDataSetChanged();
        lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);
        lv_Transactions.setSelectionFromTop(index, top);
    }

    public void loadDocuments() {
        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants
                .ENTITY_TYPE_SAVINGS, savingsAccountNumber);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, documentListFragment);
        fragmentTransaction.commit();
    }

    public void approveSavings() {
        SavingsAccountApprovalFragment savingsAccountApproval = SavingsAccountApprovalFragment
                .newInstance(savingsAccountNumber, savingsAccountType);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, savingsAccountApproval);
        fragmentTransaction.commit();
    }

    public void activateSavings() {
        SavingsAccountActivateFragment savingsAccountApproval = SavingsAccountActivateFragment
                .newInstance(savingsAccountNumber, savingsAccountType);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, savingsAccountApproval);
        fragmentTransaction.commit();
    }

    public void loadSavingsDataTables() {
        DataTableFragment loanAccountFragment = DataTableFragment.newInstance(Constants
                .DATA_TABLE_NAME_SAVINGS, savingsAccountNumber);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.container, loanAccountFragment);
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
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        if (savingsAccountWithAssociations != null) {

            this.savingsAccountWithAssociations = savingsAccountWithAssociations;

            tv_clientName.setText(savingsAccountWithAssociations.getClientName());
            tv_savingsProductName.setText(savingsAccountWithAssociations.getSavingsProductName());
            tv_savingsAccountNumber.setText(savingsAccountWithAssociations.getAccountNo());

            if (savingsAccountWithAssociations.getSummary().getTotalInterestEarned() != null) {
                tv_interestEarned.setText(String.valueOf(savingsAccountWithAssociations
                        .getSummary().getTotalInterestEarned()));
            } else {
                tv_interestEarned.setText("0.0");
            }

            tv_savingsAccountBalance.setText(String.valueOf(savingsAccountWithAssociations
                    .getSummary().getAccountBalance()));

            if (savingsAccountWithAssociations.getSummary().getTotalDeposits() != null) {
                tv_totalDeposits.setText(String.valueOf(savingsAccountWithAssociations
                        .getSummary().getTotalDeposits()));
            } else {
                tv_totalDeposits.setText("0.0");
            }

            if (savingsAccountWithAssociations.getSummary().getTotalWithdrawals() != null) {
                tv_totalWithdrawals.setText(String.valueOf(savingsAccountWithAssociations
                        .getSummary().getTotalWithdrawals()));
            } else {
                tv_totalWithdrawals.setText("0.0");
            }

            savingsAccountTransactionsListAdapter = new
                    SavingsAccountTransactionsListAdapter(getActivity(),
                    savingsAccountWithAssociations.getTransactions().size() < last ?
                            savingsAccountWithAssociations.getTransactions() :
                            savingsAccountWithAssociations.getTransactions().subList(initial, last)
            );
            lv_Transactions.setAdapter(savingsAccountTransactionsListAdapter);

            // Cache transactions here
            listOfAllTransactions.addAll(savingsAccountWithAssociations.getTransactions());

            lv_Transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int i, long l) {

                                    /*
                                     On Click at a Savings Account Transaction
                                     1. get the transactionId of that transaction
                                     2. get the account Balance after that transaction
                                    */
                    int transactionId = listOfAllTransactions.get(i).getId();
                    double runningBalance = listOfAllTransactions.get(i).getRunningBalance();

                    //Display them as a Formatted string in a toast message
                    Toast.makeText(getActivity(), String.format(getResources()
                            .getString(R.string.savings_transaction_detail), transactionId,
                            runningBalance), Toast.LENGTH_LONG).show();
                }
            });

            if (savingsAccountWithAssociations.getStatus().getSubmittedAndPendingApproval()) {
                bt_approve_saving.setEnabled(true);
                bt_deposit.setVisibility(View.GONE);
                bt_withdrawal.setVisibility(View.GONE);
                bt_approve_saving.setText(getResources().getString(R.string.approve_savings));
                processSavingTransactionAction = ACTION_APPROVE_SAVINGS;
            } else if (!(savingsAccountWithAssociations.getStatus().getActive())) {
                bt_approve_saving.setEnabled(true);
                bt_deposit.setVisibility(View.GONE);
                bt_withdrawal.setVisibility(View.GONE);
                bt_approve_saving.setText(getResources().getString(R.string.activate_savings));
                processSavingTransactionAction = ACTION_ACTIVATE_SAVINGS;
            } else if (savingsAccountWithAssociations.getStatus().getClosed()) {
                bt_approve_saving.setEnabled(false);
                bt_deposit.setVisibility(View.GONE);
                bt_withdrawal.setVisibility(View.GONE);
                bt_approve_saving
                        .setText(getResources().getString(R.string.savings_account_closed));
            } else {
                inflateSavingsAccountSummary();
                bt_approve_saving.setVisibility(View.GONE);

            }
            enableInfiniteScrollOfTransactions();
        }
    }

    @Override
    public void showSavingsActivatedSuccessfully(GenericResponse genericResponse) {
        Toast.makeText(getActivity(), getResources().getString(R.string.savings_account_activated),
                Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showFetchingError(int s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showFetchingError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSavingAccountSummaryPresenter.detachView();
    }

    public interface OnFragmentInteractionListener {
        void doTransaction(SavingsAccountWithAssociations savingsAccountWithAssociations, String
                transactionType, DepositType accountType);
    }

    // Takes ScreenShot of Relative layout
    public Bitmap layoutToImage() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "Mifos Docs");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        RelativeLayout relativeLayout = rootView.findViewById(R.id.savings_account_summary_pdf_rl);
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.setDrawingCacheBackgroundColor(getResources().getColor(R.color.white));
        relativeLayout.buildDrawingCache();
        Bitmap bm = relativeLayout.getDrawingCache();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return bm;
    }

    //Converts image to pdf and deletes image
    public void createPDF() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Bitmap HeadBitmap = layoutToImage();
            Bitmap ListBitmap = getWholeListViewItemsToBitmap();
            PdfDocument pdfDocument = null;
            pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                    HeadBitmap.getWidth() + 20, HeadBitmap.getHeight() +
                    ListBitmap.getHeight() + 20, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            page.getCanvas().drawBitmap(HeadBitmap, 0, 0, null);
            page.getCanvas().drawBitmap(ListBitmap, 0, HeadBitmap.getHeight(), null);
            ListBitmap.recycle();
            pdfDocument.finishPage(page);
            String filePath = directory + File.separator + tv_clientName.getText() + ".pdf";
            File myFile = new File(filePath);
            try {
                pdfDocument.writeTo(new FileOutputStream(myFile));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in saving PDF", Toast.LENGTH_SHORT).show();
            }
            pdfDocument.close();
            Toast.makeText(getContext(), "Pdf saved to " + filePath,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "This feature is not supported below Android Kitkat(4.4)",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getWholeListViewItemsToBitmap() {
        ListView listview = lv_Transactions;
        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView(i, null, listview);
            childView.measure(View.MeasureSpec.makeMeasureSpec(listview.getWidth(),
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(),
                    childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }
        Bitmap bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }
        return bigbitmap;
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PackageManager.PERMISSION_GRANTED);
        }
    }

    public Boolean checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int perm1 = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int perm2 = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return perm1 == PackageManager.PERMISSION_GRANTED
                    && perm2 == PackageManager.PERMISSION_GRANTED;
        } else {
            return false;
        }
    }
}
