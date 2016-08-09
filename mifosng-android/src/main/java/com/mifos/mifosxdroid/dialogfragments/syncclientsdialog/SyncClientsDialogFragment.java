package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.sync.SyncClientInformationStatus;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
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

    private List<Client> mClients;

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

        mSyncClientsDialogPresenter
                .syncClientDetailsAndClientAccounts(mClients.get(mClientSyncIndex));

        return rootView;
    }

    @OnClick(R.id.btn_cancel)
    void OnClickCancelButton() {
        getDialog().dismiss();
    }

    /**
     *
     */
    @Override
    public void syncClientAndInformation() {
        mClientSyncIndex = mClientSyncIndex + 1;
        mLoanRepaymentSyncIndex = 0;
        mLoanSyncIndex = 0;
        mSyncClientsDialogPresenter.syncClientDetailsAndClientAccounts(mClients
                .get(mClientSyncIndex));
    }

    @Override
    public void showClientAndAccountsSyncedSuccessfully(Client client,
                                                        ClientAccounts clientAccounts) {
        mSyncClientInformationStatus.setClientAndAccountsStatus(true);
        mLoanSyncIndex = clientAccounts.getLoanAccounts().size();
        mLoanRepaymentSyncIndex = clientAccounts.getLoanAccounts().size();
        mSyncClientsDialogPresenter.syncLoanAndLoanRepayment(clientAccounts.getLoanAccounts());
    }

    @Override
    public void showLoanSyncSuccessfully(LoanWithAssociations loanWithAssociations) {
        mLoanSyncIndex = mLoanSyncIndex - 1;
        if (mLoanSyncIndex == 0) {
            mSyncClientInformationStatus.setLoanAccountSummaryStatus(true);
        }

        if (mSyncClientsDialogPresenter.isClientInformationSync(mSyncClientInformationStatus)) {
            syncClientAndInformation();
        }
    }

    @Override
    public void showLoanRepaymentSyncSuccessfully(LoanRepaymentTemplate loanRepaymentTemplate) {
        mLoanRepaymentSyncIndex = mLoanRepaymentSyncIndex - 1;
        if (mLoanRepaymentSyncIndex == 0) {
            mSyncClientInformationStatus.setLoanRepaymentTemplateStatus(true);
        }

        if (mSyncClientsDialogPresenter.isClientInformationSync(mSyncClientInformationStatus)) {
            syncClientAndInformation();
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
