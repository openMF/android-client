package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 11/09/16.
 */
public class SyncGroupsDialogFragment extends DialogFragment implements SyncGroupsDialogMvpView {


    @BindView(R.id.tv_sync_title)
    TextView tv_sync_title;

    @BindView(R.id.tv_group_name)
    TextView tv_syncing_group_name;

    @BindView(R.id.tv_total_groups)
    TextView tv_total_groups;

    @BindView(R.id.tv_syncing_group)
    TextView tv_syncing_group;

    @BindView(R.id.pb_sync_group)
    ProgressBar pb_syncing_group;

    @BindView(R.id.tv_total_progress)
    TextView tv_total_progress;

    @BindView(R.id.pb_total_sync_group)
    ProgressBar pb_total_sync_group;

    @BindView(R.id.tv_syncing_client)
    TextView tv_syncing_client;

    @BindView(R.id.pb_sync_client)
    ProgressBar pb_syncing_client;

    @BindView(R.id.tv_sync_failed)
    TextView tv_sync_failed;

    @BindView(R.id.btn_hide)
    Button btn_hide;

    @BindView(R.id.btn_cancel)
    Button btn_cancel;

    @Inject
    SyncGroupsDialogPresenter syncGroupsDialogPresenter;

    View rootView;

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
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_groups, container, false);
        ButterKnife.bind(this, rootView);
        syncGroupsDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Groups
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            syncGroupsDialogPresenter.startSyncingGroups(groups);
        } else {
            showNetworkIsNotAvailable();
            getActivity().getSupportFragmentManager().popBackStack();
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
        pb_total_sync_group.setMax(groups.size());
        String total_groups = groups.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.groups);
        tv_total_groups.setText(total_groups);
        tv_sync_failed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingGroup(String groupName) {
        tv_syncing_group.setText(groupName);
        tv_syncing_group_name.setText(groupName);
    }

    @Override
    public void showSyncedFailedGroups(int failedCount) {
        tv_sync_failed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncGroupProgressBar(int total) {
        pb_syncing_group.setMax(total);
    }

    @Override
    public void setClientSyncProgressBarMax(int count) {
        pb_syncing_client.setMax(count);
    }

    @Override
    public void updateClientSyncProgressBar(int i) {
        pb_syncing_client.setProgress(i);
    }

    @Override
    public void updateSingleSyncGroupProgressBar(int count) {
        pb_syncing_group.setProgress(count);
    }

    @Override
    public void updateTotalSyncGroupProgressBarAndCount(int count) {
        pb_total_sync_group.setProgress(count);
        String total_sync_count = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + groups.size();
        tv_total_progress.setText(total_sync_count);
    }

    @Override
    public int getMaxSingleSyncGroupProgressBar() {
        return pb_syncing_group.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources()
                .getString(R.string.error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGroupsSyncSuccessfully() {
        btn_cancel.setVisibility(View.INVISIBLE);
        btn_hide.setText(getResources().getString(R.string.dialog_action_ok));
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
        Toaster.show(rootView, s);
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
