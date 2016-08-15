package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;

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

    @BindView(R.id.btn_hide)
    Button btn_hide;

    @BindView(R.id.btn_cancel)
    Button btn_cancel;

    @Inject
    SyncClientsDialogPresenter mSyncClientsDialogPresenter;

    View rootView;

    private List<Client> mClientList;


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
        if (getArguments() != null) {
            mClientList = getArguments().getParcelableArrayList(Constants.CLIENT);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_clients, container, false);
        ButterKnife.bind(this, rootView);
        mSyncClientsDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Clients
        if (Network.isOnline(getActivity())) {
            mSyncClientsDialogPresenter.startSyncingClients(mClientList);
        } else {
            showNetworkIsNotAvailable();
            dismissDialog();
        }

        return rootView;
    }

    @OnClick(R.id.btn_cancel)
    void onClickCancelButton() {
        dismissDialog();
    }

    @OnClick(R.id.btn_hide)
    void onClickHideButton() {
        if (btn_hide.getText().equals(getResources().getString(R.string.dialog_action_ok))) {
            dismissDialog();
        } else {
            hideDialog();
        }
    }


    @Override
    public void showUI() {
        pb_total_sync_client.setMax(mClientList.size());
        String total_clients = mClientList.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.clients);
        tv_total_clients.setText(total_clients);
        tv_sync_failed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingClient(String clientName) {
        tv_syncing_client.setText(clientName);
        tv_syncing_client_name.setText(clientName);
    }

    @Override
    public void showSyncedFailedClients(int failedCount) {
        tv_sync_failed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncClientProgressBar(int total) {
        pb_syncing_client.setMax(total);
    }

    @Override
    public void updateSingleSyncClientProgressBar(int count) {
        pb_syncing_client.setProgress(count);
    }

    @Override
    public void updateTotalSyncClientProgressBarAndCount(int count) {
        pb_total_sync_client.setProgress(count);
        String total_sync_count = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + mClientList.size();
        tv_total_progress.setText(total_sync_count);
    }

    @Override
    public int getMaxSingleSyncClientProgressBar() {
        return pb_syncing_client.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources().getString(R.string
                .error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showClientsSyncSuccessfully() {
        btn_cancel.setVisibility(View.INVISIBLE);
        btn_hide.setText(getResources().getString(R.string.dialog_action_ok));
    }

    @Override
    public void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void showDialog() {
        getDialog().show();
    }

    @Override
    public void hideDialog() {
        getDialog().hide();
    }

    @Override
    public void showError(int s) {
        Toaster.show(rootView, s);
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
