package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import com.mifos.mifosxdroid.adapters.SyncLoanRepaymentAdapter;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 28/07/16.
 */
public class SyncLoanRepaymentTransactionFragment extends MifosBaseFragment implements
        SyncLoanRepaymentTransactionMvpView, DialogInterface.OnClickListener {

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
    SyncLoanRepaymentTransactionPresenter mSyncLoanRepaymentTransactionPresenter;

    @Inject
    SyncLoanRepaymentAdapter mSyncLoanRepaymentAdapter;

    private View rootView;

    private List<LoanRepaymentRequest> mLoanRepaymentRequests;

    private int mClientSyncIndex = 0;

    public static SyncLoanRepaymentTransactionFragment newInstance() {
        Bundle arguments = new Bundle();
        SyncLoanRepaymentTransactionFragment fragment = new SyncLoanRepaymentTransactionFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mLoanRepaymentRequests = new ArrayList<>();
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_syncpayload, container, false);

        ButterKnife.bind(this, rootView);
        mSyncLoanRepaymentTransactionPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_loan_repayment.setLayoutManager(mLayoutManager);
        rv_loan_repayment.setHasFixedSize(true);
        rv_loan_repayment.setAdapter(mSyncLoanRepaymentAdapter);


        /**
         * Loading All Loan Repayment Offline Transaction from Database
         */
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Loading LoanRepayment Transactions and PaymentTypeOptions From Database
                mSyncLoanRepaymentTransactionPresenter.loadDatabaseLoanRepaymentTransactions();
                mSyncLoanRepaymentTransactionPresenter.loanPaymentTypeOption();

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Loading LoanRepayment Transactions  and PaymentTypeOptions From Database
        mSyncLoanRepaymentTransactionPresenter.loadDatabaseLoanRepaymentTransactions();
        mSyncLoanRepaymentTransactionPresenter.loanPaymentTypeOption();

        return rootView;
    }

    /**
     * Show when Database response is null or failed to fetch the List<LoanRepayment>
     * Onclick Send Fresh Request for List<LoanRepayment>.
     */
    @OnClick(R.id.noPayloadIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mSyncLoanRepaymentTransactionPresenter.loadDatabaseLoanRepaymentTransactions();
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
                //TODO Write Negative Button Click Event Logic
                break;
            case DialogInterface.BUTTON_POSITIVE:
                PrefManager.setUserStatus(Constants.USER_ONLINE);
                if (mLoanRepaymentRequests.size() != 0) {
                    mClientSyncIndex = 0;
                    syncGroupPayload();
                } else {
                    Toaster.show(rootView,
                            getActivity().getResources().getString(R.string.nothing_to_sync));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showLoanRepaymentTransactions(List<LoanRepaymentRequest> loanRepaymentRequests) {
        mLoanRepaymentRequests = loanRepaymentRequests;
        if (loanRepaymentRequests.size() == 0) {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.no_repayment_to_sync));
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mSyncLoanRepaymentAdapter.setLoanRepaymentRequests(loanRepaymentRequests);
        }
    }

    @Override
    public void showPaymentTypeOption(List<PaymentTypeOption> paymentTypeOptions) {
        mSyncLoanRepaymentAdapter.setPaymentTypeOptions(paymentTypeOptions);
    }

    @Override
    public void showPaymentSubmittedSuccessfully() {
        mSyncLoanRepaymentTransactionPresenter
                .deleteAndUpdateLoanRepayments(mLoanRepaymentRequests
                        .get(mClientSyncIndex).getLoanId());
    }

    @Override
    public void showPaymentFailed(String errorMessage) {
        LoanRepaymentRequest loanRepaymentRequest = mLoanRepaymentRequests.get(mClientSyncIndex);
        loanRepaymentRequest.setErrorMessage(errorMessage);
        mSyncLoanRepaymentTransactionPresenter.updateLoanRepayment(loanRepaymentRequest);
    }

    @Override
    public void showLoanRepaymentUpdated(LoanRepaymentRequest loanRepaymentRequest) {
        mLoanRepaymentRequests.set(mClientSyncIndex, loanRepaymentRequest);
        mSyncLoanRepaymentAdapter.notifyDataSetChanged();

        mClientSyncIndex = mClientSyncIndex + 1;
        if (mLoanRepaymentRequests.size() != mClientSyncIndex) {
            syncGroupPayload();
        }
    }

    @Override
    public void showLoanRepaymentDeletedAndUpdateLoanRepayment(List<LoanRepaymentRequest>
                                                                       loanRepaymentRequests) {
        mClientSyncIndex = 0;
        this.mLoanRepaymentRequests = loanRepaymentRequests;
        mSyncLoanRepaymentAdapter.setLoanRepaymentRequests(loanRepaymentRequests);
        if (mLoanRepaymentRequests.size() != 0) {
            syncGroupPayload();
        } else {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.all_repayment_synced));
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }


    @Override
    public void showError(int stringId) {
        ll_error.setVisibility(View.VISIBLE);
        String message =
                stringId + getActivity().getResources().getString(R.string.click_to_refresh);
        mNoPayloadText.setText(message);
        Toaster.show(rootView, stringId);
    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && mSyncLoanRepaymentAdapter.getItemCount() == 0) {
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
                    if (mLoanRepaymentRequests.size() != 0) {
                        mClientSyncIndex = 0;
                        syncGroupPayload();
                    } else {
                        Toaster.show(rootView,
                                getActivity().getResources().getString(R.string.nothing_to_sync));
                    }
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

    public void syncGroupPayload() {
        for (int i = 0; i < mLoanRepaymentRequests.size(); ++i) {
            if (mLoanRepaymentRequests.get(i).getErrorMessage() == null) {
                mSyncLoanRepaymentTransactionPresenter.syncLoanRepayment(mLoanRepaymentRequests
                        .get(i)
                        .getLoanId(), mLoanRepaymentRequests.get(i));
                mClientSyncIndex = i;
                break;
            } else {
                Log.d(LOG_TAG,
                        getActivity().getResources().getString(R.string.error_fix_before_sync) +
                        mLoanRepaymentRequests.get(i).getErrorMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSyncLoanRepaymentTransactionPresenter.detachView();
    }
}
