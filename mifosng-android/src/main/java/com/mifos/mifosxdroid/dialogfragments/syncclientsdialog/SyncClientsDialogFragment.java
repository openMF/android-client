package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncClientsBinding;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;



/**
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogFragment extends DialogFragment implements SyncClientsDialogMvpView {


    public static final String LOG_TAG = SyncClientsDialogFragment.class.getSimpleName();
    private DialogFragmentSyncClientsBinding binding;

    @Inject
    SyncClientsDialogPresenter mSyncClientsDialogPresenter;

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
        binding = DialogFragmentSyncClientsBinding.inflate(inflater, container, false);
        mSyncClientsDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Clients
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            mSyncClientsDialogPresenter.startSyncingClients(mClientList);
        } else {
            showNetworkIsNotAvailable();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCancel.setOnClickListener(view1 -> onClickCancelButton());
        binding.btnHide.setOnClickListener(view1 -> onClickHideButton());
    }

    void onClickCancelButton() {
        dismissDialog();
    }

    void onClickHideButton() {
        if (binding.btnHide.getText().equals(getResources().getString(R.string.dialog_action_ok))) {
            dismissDialog();
        } else {
            hideDialog();
        }
    }


    @Override
    public void showUI() {
        binding.pbTotalSyncClient.setMax(mClientList.size());
        String total_clients = mClientList.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.clients);
        binding.tvTotalClients.setText(total_clients);
        binding.tvSyncFailed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingClient(String clientName) {
        binding.tvSyncingClient.setText(clientName);
        binding.tvClientName.setText(clientName);
    }

    @Override
    public void showSyncedFailedClients(int failedCount) {
        binding.tvSyncFailed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncClientProgressBar(int total) {
        binding.pbSyncClient.setMax(total);
    }

    @Override
    public void updateSingleSyncClientProgressBar(int count) {
        binding.pbSyncClient.setProgress(count);
    }

    @Override
    public void updateTotalSyncClientProgressBarAndCount(int count) {
        binding.pbTotalSyncClient.setProgress(count);
        String total_sync_count = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + mClientList.size();
        binding.tvTotalProgress.setText(total_sync_count);
    }

    @Override
    public int getMaxSingleSyncClientProgressBar() {
        return binding.pbSyncClient.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources().getString(R.string
                .error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showClientsSyncSuccessfully() {
        binding.btnCancel.setVisibility(View.INVISIBLE);
        binding.btnHide.setText(getResources().getString(R.string.dialog_action_ok));
    }

    @Override
    public Boolean isOnline() {
        return Network.isOnline(getActivity());
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
        Toaster.show(binding.getRoot(), s);
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
