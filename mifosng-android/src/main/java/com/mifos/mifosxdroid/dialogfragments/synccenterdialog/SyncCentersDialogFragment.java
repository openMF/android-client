package com.mifos.mifosxdroid.dialogfragments.synccenterdialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
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
import com.mifos.objects.group.Center;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncCentersDialogFragment extends DialogFragment implements SyncCenterDialogMvpView {

    @BindView(R.id.tv_sync_title)
    TextView tvSyncTitle;

    @BindView(R.id.tv_center_name)
    TextView tvSyncingCenterName;

    @BindView(R.id.tv_total_centers)
    TextView tvTotalCenters;

    @BindView(R.id.tv_syncing_center)
    TextView tvSyncingCenter;

    @BindView(R.id.pb_sync_center)
    ProgressBar pbSyncingCenter;

    @BindView(R.id.tv_total_progress)
    TextView tvTotalProgress;

    @BindView(R.id.pb_total_sync_center)
    ProgressBar pbTotalSyncCenter;

    @BindView(R.id.tv_syncing_group)
    TextView tvSyncingGroup;

    @BindView(R.id.pb_sync_group)
    ProgressBar pbSyncingGroup;

    @BindView(R.id.tv_syncing_client)
    TextView tvSyncingClient;

    @BindView(R.id.pb_sync_client)
    ProgressBar pbSyncingClient;

    @BindView(R.id.tv_sync_failed)
    TextView tvSyncFailed;

    @BindView(R.id.btn_hide)
    Button btnHide;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @Inject
    SyncCenterDialogPresenter syncCentersDialogPresenter;

    View rootView;
    private List<Center> mCenterList;

    public static SyncCentersDialogFragment newInstance(List<Center> center) {
        SyncCentersDialogFragment syncCentersDialogFragment = new SyncCentersDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.CENTER, (ArrayList<? extends Parcelable>) center);
        syncCentersDialogFragment.setArguments(args);
        return syncCentersDialogFragment;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.Channel_Name);
            String description = getString(R.string.Channel_Description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new
                    NotificationChannel(getString(R.string.Channel_ID), name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    void showProgressInNotification() {
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.
                from(getActivity());

        final NotificationCompat.Builder builder = new NotificationCompat.
                Builder(getActivity(), getString(R.string.Channel_ID));
        builder.setSmallIcon(R.drawable.mifos_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);
        new Thread(new Runnable() {
            public void run() {
                int MAX_PROGRESS = Integer.parseInt(getString(R.string.max_progress));
                int CURRENT_PROGRESS = Integer.parseInt(getString(R.string.current_progress));
                while (syncCentersDialogPresenter.mCenterSyncIndex < mCenterList.size()) {
                    builder.setContentTitle("Syncing Centers " +
                            (syncCentersDialogPresenter.mCenterSyncIndex ) +
                            "/" + mCenterList.size());
                    builder.setContentText(CURRENT_PROGRESS + "%");
                    builder.setProgress(MAX_PROGRESS, CURRENT_PROGRESS, false);
                    notificationManager.notify(0, builder.build());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e("Error: ", e.toString());
                    }
                    float individualCenterProgress = pbTotalSyncCenter.getProgress();
                    float individualCenterMax = pbTotalSyncCenter.getMax();
                    float individualCenterPercent =
                            (individualCenterProgress / individualCenterMax) * 100;
                    CURRENT_PROGRESS = (int) individualCenterPercent;
                }

                builder.setContentTitle(mCenterList.size() + " Centers Synced ");
                builder.setContentText("Sync Completed").setProgress(0, 0, false);
                notificationManager.notify(0, builder.build());
            }
        }).start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            mCenterList = getArguments().getParcelableArrayList(Constants.CENTER);
        }
        super.onCreate(savedInstanceState);
        createNotificationChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_centers, container, false);
        ButterKnife.bind(this, rootView);
        syncCentersDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Centers
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            syncCentersDialogPresenter.startSyncingCenters(mCenterList);
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
        if (btnHide.getText().equals(getResources().getString(R.string.dialog_action_ok))) {
            dismissDialog();
        } else {
            hideDialog();
            showProgressInNotification();
        }
    }

    @Override
    public void showUI() {
        pbTotalSyncCenter.setMax(mCenterList.size());
        String totalCenters = mCenterList.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.centers);
        tvTotalCenters.setText(totalCenters);
        tvSyncFailed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingCenter(String centerName) {
        tvSyncingCenter.setText(centerName);
        tvSyncingCenterName.setText(centerName);
    }

    @Override
    public void showSyncedFailedCenters(int failedCount) {
        tvSyncFailed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncCenterProgressBar(int total) {
        pbSyncingCenter.setMax(total);
    }

    @Override
    public void updateSingleSyncCenterProgressBar(int count) {
        pbSyncingCenter.setProgress(count);
    }

    @Override
    public void updateTotalSyncCenterProgressBarAndCount(int count) {
        pbTotalSyncCenter.setProgress(count);
        String totalSyncCount = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + mCenterList.size();
        tvTotalProgress.setText(totalSyncCount);
    }

    @Override
    public int getMaxSingleSyncCenterProgressBar() {
        return pbSyncingCenter.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources().getString(R.string
                .error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCentersSyncSuccessfully() {
        btnCancel.setVisibility(View.INVISIBLE);
        btnHide.setText(getResources().getString(R.string.dialog_action_ok));
    }

    @Override
    public void setGroupSyncProgressBarMax(int count) {
        pbSyncingGroup.setMax(count);
    }

    @Override
    public void updateGroupSyncProgressBar(int i) {
        pbSyncingGroup.setProgress(i);
    }

    @Override
    public void setClientSyncProgressBarMax(int count) {
        pbSyncingClient.setMax(count);
    }

    @Override
    public void updateClientSyncProgressBar(int i) {
        pbSyncingClient.setProgress(i);
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
        syncCentersDialogPresenter.detachView();
    }
}
