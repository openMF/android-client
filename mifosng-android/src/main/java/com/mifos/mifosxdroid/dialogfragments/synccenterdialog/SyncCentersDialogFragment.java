package com.mifos.mifosxdroid.dialogfragments.synccenterdialog;

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
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncCentersBinding;
import com.mifos.objects.group.Center;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class SyncCentersDialogFragment extends DialogFragment implements SyncCenterDialogMvpView {
    private DialogFragmentSyncCentersBinding binding;

    @Inject
    SyncCenterDialogPresenter syncCentersDialogPresenter;

    private List<Center> mCenterList;

    public static SyncCentersDialogFragment newInstance(List<Center> center) {
        SyncCentersDialogFragment syncCentersDialogFragment = new SyncCentersDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.CENTER, (ArrayList<? extends Parcelable>) center);
        syncCentersDialogFragment.setArguments(args);
        return syncCentersDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            mCenterList = getArguments().getParcelableArrayList(Constants.CENTER);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentSyncCentersBinding.inflate(inflater, container, false);
        syncCentersDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Centers
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            syncCentersDialogPresenter.startSyncingCenters(mCenterList);
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
        binding.pbTotalSyncCenter.setMax(mCenterList.size());
        String totalCenters = mCenterList.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.centers);
        binding.tvTotalCenters.setText(totalCenters);
        binding.tvSyncFailed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingCenter(String centerName) {
        binding.tvSyncingCenter.setText(centerName);
        binding.tvCenterName.setText(centerName);
    }

    @Override
    public void showSyncedFailedCenters(int failedCount) {
        binding.tvSyncFailed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncCenterProgressBar(int total) {
        binding.pbSyncCenter.setMax(total);
    }

    @Override
    public void updateSingleSyncCenterProgressBar(int count) {
        binding.pbSyncCenter.setProgress(count);
    }

    @Override
    public void updateTotalSyncCenterProgressBarAndCount(int count) {
        binding.pbTotalSyncCenter.setProgress(count);
        String totalSyncCount = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + mCenterList.size();
        binding.tvTotalProgress.setText(totalSyncCount);
    }

    @Override
    public int getMaxSingleSyncCenterProgressBar() {
        return binding.pbSyncCenter.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources().getString(R.string
                .error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCentersSyncSuccessfully() {
        binding.btnCancel.setVisibility(View.INVISIBLE);
        binding.btnHide.setText(getResources().getString(R.string.dialog_action_ok));
    }

    @Override
    public void setGroupSyncProgressBarMax(int count) {
        binding.pbSyncGroup.setMax(count);
    }

    @Override
    public void updateGroupSyncProgressBar(int i) {
        binding.pbSyncGroup.setProgress(i);
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
        syncCentersDialogPresenter.detachView();
    }
}
