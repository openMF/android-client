package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog;

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
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncGroupsBinding;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Rajan Maurya on 11/09/16.
 */
public class SyncGroupsDialogFragment extends DialogFragment implements SyncGroupsDialogMvpView {

    private DialogFragmentSyncGroupsBinding binding;

    @Inject
    SyncGroupsDialogPresenter syncGroupsDialogPresenter;

    private List<Group> groups;

    public static SyncGroupsDialogFragment newInstance(List<Group> groups) {
        SyncGroupsDialogFragment syncGroupsDialogFragment = new SyncGroupsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.GROUPS, (ArrayList<? extends Parcelable>) groups);
        syncGroupsDialogFragment.setArguments(args);
        return syncGroupsDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            groups = getArguments().getParcelableArrayList(Constants.GROUPS);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentSyncGroupsBinding.inflate(inflater, container, false);
        syncGroupsDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Groups
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            syncGroupsDialogPresenter.startSyncingGroups(groups);
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
        binding.pbTotalSyncGroup.setMax(groups.size());
        String total_groups = groups.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.groups);
        binding.tvTotalGroups.setText(total_groups);
        binding.tvSyncFailed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingGroup(String groupName) {
        binding.tvSyncingGroup.setText(groupName);
        binding.tvGroupName.setText(groupName);
    }

    @Override
    public void showSyncedFailedGroups(int failedCount) {
        binding.tvSyncFailed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncGroupProgressBar(int total) {
        binding.pbSyncGroup.setMax(total);
    }

    @Override
    public void setClientSyncProgressBarMax(int count) {
        binding.pbSyncClient.setMax(count);
    }

    @Override
    public void updateClientSyncProgressBar(int i) {
        binding.pbSyncClient.setProgress(i);
    }

    @Override
    public void updateSingleSyncGroupProgressBar(int count) {
        binding.pbSyncGroup.setProgress(count);
    }

    @Override
    public void updateTotalSyncGroupProgressBarAndCount(int count) {
        binding.pbTotalSyncGroup.setProgress(count);
        String total_sync_count = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + groups.size();
        binding.tvTotalProgress.setText(total_sync_count);
    }

    @Override
    public int getMaxSingleSyncGroupProgressBar() {
        return binding.pbSyncGroup.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources()
                .getString(R.string.error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGroupsSyncSuccessfully() {
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
        syncGroupsDialogPresenter.detachView();
    }
}
