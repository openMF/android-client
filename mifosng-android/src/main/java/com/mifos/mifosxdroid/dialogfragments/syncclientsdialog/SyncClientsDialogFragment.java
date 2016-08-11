package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.client.Client;
import com.mifos.objects.sync.SyncClientInformationStatus;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogFragment extends DialogFragment implements SyncClientsDialogMvpView {


    public static final String LOG_TAG = SyncClientsDialogFragment.class.getSimpleName();

    @BindView(R.id.tv_sync_title)
    TextView tv_sync_title;

    @BindView(R.id.tv_client_name)
    TextView tv_syncing_client_name;

    @BindView(R.id.tv_total_clients)
    TextView tv_total_clients;

    @BindView(R.id.tv_syncing_client)
    TextView tv_syncing_client;

    @BindView(R.id.pb_sync_client)
    ProgressBar pb_syncing_client;

    @BindView(R.id.tv_total_progress)
    TextView tv_total_progress;

    @BindView(R.id.pb_total_sync_client)
    ProgressBar pb_total_sync_client;

    @BindView(R.id.tv_sync_failed)
    TextView tv_sync_failed;

    @Inject
    SyncClientsDialogPresenter mSyncClientsDialogPresenter;

    private View rootView;

    private List<Client> mClients, mFailedSyncClient;

    private SyncClientInformationStatus mSyncClientInformationStatus;

    private int mClientSyncIndex, mLoanSyncIndex, mLoanRepaymentSyncIndex  = 0;


    public static SyncClientsDialogFragment newInstance(List<Client> client) {
        SyncClientsDialogFragment syncClientsDialogFragment = new SyncClientsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.CLIENT, (ArrayList<? extends Parcelable>) client);
        syncClientsDialogFragment.setArguments(args);
        return syncClientsDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mSyncClientInformationStatus = new SyncClientInformationStatus();
        mFailedSyncClient = new ArrayList<>();
        if (getArguments() != null)
            mClients = getArguments().getParcelableArrayList(Constants.CLIENT);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_clients, container, false);
        ButterKnife.bind(this, rootView);
        mSyncClientsDialogPresenter.attachView(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //Sync Client Account
        if (!mClients.isEmpty()) {
            mSyncClientsDialogPresenter.syncClientAccounts(mClients.get(mClientSyncIndex).getId());
        }

        return rootView;
    }

    @OnClick(R.id.btn_cancel)
    void OnClickCancelButton() {
        getDialog().dismiss();
    }

    @OnClick(R.id.btn_hide)
    void OnClickHideButton() {
        getDialog().hide();
    }

    /**
     *
     */
    @Override
    public void syncClientInformation() {
        mClientSyncIndex = mClientSyncIndex + 1;
        mLoanRepaymentSyncIndex = 0;
        mLoanSyncIndex = 0;
        if (mClients.size() != mClientSyncIndex) {
            mSyncClientsDialogPresenter.syncClientAccounts(mClients.get(mClientSyncIndex).getId());
        } else {
            Log.d(LOG_TAG, getActivity().getResources().getString(R.string.nothing_to_sync));
        }
    }

    @Override
    public void showClientAccountsSyncedSuccessfully(ClientAccounts clientAccounts) {
        mSyncClientInformationStatus.setClientAccountsStatus(true);
        List<LoanAccount> loanAccounts = mSyncClientsDialogPresenter
                .getActiveLoanAccounts(clientAccounts.getLoanAccounts());
        if (!loanAccounts.isEmpty()) {
            mLoanSyncIndex = loanAccounts.size();
            mLoanRepaymentSyncIndex = loanAccounts.size();
            mSyncClientsDialogPresenter.syncLoanAndLoanRepayment(loanAccounts);
        }
    }

    @Override
    public void showLoanSyncSuccessfully() {
        mLoanSyncIndex = mLoanSyncIndex - 1;
        if (mLoanSyncIndex == 0) {
            mSyncClientInformationStatus.setLoanAccountSummaryStatus(true);
        }

        if (mLoanSyncIndex == 0 && mLoanRepaymentSyncIndex == 0) {
            mClients.get(mClientSyncIndex).setSync(true);
            mSyncClientsDialogPresenter.syncClient(mClients.get(mClientSyncIndex));
        }
    }

    @Override
    public void showLoanRepaymentSyncSuccessfully() {
        mLoanRepaymentSyncIndex = mLoanRepaymentSyncIndex - 1;
        if (mLoanRepaymentSyncIndex == 0) {
            mSyncClientInformationStatus.setLoanRepaymentTemplateStatus(true);
        }

        if (mLoanSyncIndex == 0 && mLoanRepaymentSyncIndex == 0) {
            mClients.get(mClientSyncIndex).setSync(true);
            mSyncClientsDialogPresenter.syncClient(mClients.get(mClientSyncIndex));
        }
    }

    @Override
    public void showClientSyncSuccessfully() {
        mSyncClientInformationStatus.setClientStatus(true);
        if (mSyncClientsDialogPresenter.isClientInformationSync(mSyncClientInformationStatus)) {
            syncClientInformation();
        }
    }

    @Override
    public void showError(int s) {

    }

    @Override
    public void showProgressbar(boolean b) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSyncClientsDialogPresenter.detachView();
    }
}
