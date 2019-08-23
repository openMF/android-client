package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SyncSavingsAccountTransactionAdapter;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 19/08/16.
 */
public class SyncSavingsAccountTransactionFragment extends MifosBaseFragment implements
        SyncSavingsAccountTransactionMvpView, SwipeRefreshLayout.OnRefreshListener,
        DialogInterface.OnClickListener {


    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.rv_sync_payload)
    RecyclerView rv_loan_repayment;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noPayloadText)
    TextView mNoPayloadText;

    @BindView(R.id.noPayloadIcon)
    ImageView mNoPayloadIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    SyncSavingsAccountTransactionPresenter mSyncSavingsAccountTransactionPresenter;

    @Inject
    SyncSavingsAccountTransactionAdapter mSyncSavingsAccountTransactionAdapter;

    private View rootView;


    public static SyncSavingsAccountTransactionFragment newInstance() {
        Bundle arguments = new Bundle();
        SyncSavingsAccountTransactionFragment sync = new SyncSavingsAccountTransactionFragment();
        sync.setArguments(arguments);
        return sync;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_syncpayload, container, false);

        ButterKnife.bind(this, rootView);
        mSyncSavingsAccountTransactionPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_loan_repayment.setLayoutManager(mLayoutManager);
        rv_loan_repayment.setHasFixedSize(true);
        rv_loan_repayment.setAdapter(mSyncSavingsAccountTransactionAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);

        //Loading LoanRepayment Transactions  and PaymentTypeOptions From Database
        mSyncSavingsAccountTransactionPresenter.loadDatabaseSavingsAccountTransactions();
        mSyncSavingsAccountTransactionPresenter.loadPaymentTypeOption();

        return rootView;
    }

    /**
     * Loading All SavingsAccount Transactions from Database On SwipeRefresh
     */
    @Override
    public void onRefresh() {
        //Loading LoanRepayment Transactions and PaymentTypeOptions From Database
        mSyncSavingsAccountTransactionPresenter.loadDatabaseSavingsAccountTransactions();
        mSyncSavingsAccountTransactionPresenter.loadPaymentTypeOption();

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Show when Database response is null or failed to fetch the
     * List<SavingsAccountTransactionRequest>
     * Onclick Send Fresh Request for List<SavingsAccountTransactionRequest>.
     */
    @OnClick(R.id.noPayloadIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mSyncSavingsAccountTransactionPresenter.loadDatabaseSavingsAccountTransactions();
        mSyncSavingsAccountTransactionPresenter.loadPaymentTypeOption();
    }

    @Override
    public void showOfflineModeDialog() {
        new MaterialDialog.Builder().init(getActivity())
                .setTitle(R.string.offline_mode)
                .setMessage(R.string.dialog_message_offline_sync_alert)
                .setPositiveButton(R.string.dialog_action_go_online, this)
                .setNegativeButton(R.string.dialog_action_cancel, this)
                .createMaterialDialog()
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                PrefManager.setUserStatus(Constants.USER_ONLINE);
                checkNetworkConnectionAndSync();
                break;
            default:
                break;
        }
    }

    @Override
    public void showSavingsAccountTransactions(
            List<SavingsAccountTransactionRequest> transactions) {
        mSyncSavingsAccountTransactionAdapter.setSavingsAccountTransactions(transactions);
    }

    @Override
    public void showEmptySavingsAccountTransactions(int message) {
        ll_error.setVisibility(View.VISIBLE);
        mNoPayloadText.setText(getActivity().getResources().getString(message));
        mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
    }

    @Override
    public void showPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        mSyncSavingsAccountTransactionAdapter.setPaymentTypeOptions(paymentTypeOptions);
    }

    @Override
    public void checkNetworkConnectionAndSync() {
        if (Network.isOnline(getActivity())) {
            mSyncSavingsAccountTransactionPresenter.syncSavingsAccountTransactions();
        } else {
            Toaster.show(rootView, getResources().getString(R.string.error_network_not_available));
        }
    }

    @Override
    public void showError(int message) {
        Toaster.show(rootView, getResources().getString(message));
    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && mSyncSavingsAccountTransactionAdapter.getItemCount() == 0) {
            showMifosProgressBar();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            hideMifosProgressBar();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            switch (PrefManager.getUserStatus()) {
                case 0:
                    checkNetworkConnectionAndSync();
                    break;
                case 1:
                    showOfflineModeDialog();
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSyncSavingsAccountTransactionPresenter.detachView();
    }
}
