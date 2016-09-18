package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog;

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

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;

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
    TextView tv_total_group;

    @BindView(R.id.tv_syncing_group)
    TextView tv_syncing_group;

    @BindView(R.id.pb_sync_group)
    ProgressBar pb_syncing_group;

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
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_clients, container, false);
        ButterKnife.bind(this, rootView);
        syncGroupsDialogPresenter.attachView(this);

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
    public void showProgressbar(boolean b) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        syncGroupsDialogPresenter.detachView();
    }

    @Override
    public void showUI() {

    }

    @Override
    public void showSyncingGroup(String clientName) {

    }

    @Override
    public void showSyncedFailedGroups(int failedCount) {

    }

    @Override
    public void setMaxSingleSyncGroupProgressBar(int total) {

    }

    @Override
    public void updateSingleSyncGroupProgressBar(int i) {

    }

    @Override
    public void updateTotalSyncGroupProgressBarAndCount(int i) {

    }

    @Override
    public int getMaxSingleSyncGroupProgressBar() {
        return 0;
    }

    @Override
    public void showNetworkIsNotAvailable() {

    }

    @Override
    public void showGroupsSyncSuccessfully() {

    }

    @Override
    public Boolean isOnline() {
        return null;
    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void hideDialog() {

    }

    @Override
    public void showError(int s) {

    }
}
