package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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

import com.mifos.mifosxdroid.ClientListActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
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
        final NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(getActivity());
        Intent intent = new Intent(getActivity(), ClientListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        final NotificationCompat.Builder builder = new NotificationCompat.
                Builder(getActivity(), getString(R.string.Channel_ID));
        builder.setSmallIcon(R.drawable.mifos_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);
        new Thread(new Runnable() {
            public void run() {
                int MAX_PROGRESS = Integer.parseInt(getString(R.string.max_progress));
                int CURRENT_PROGRESS = Integer.parseInt(getString(R.string.current_progress));
                while (mSyncClientsDialogPresenter.mClientSyncIndex < mClientList.size()) {
                    builder.setContentTitle("Syncing Clients " +
                            (mSyncClientsDialogPresenter.mClientSyncIndex) +
                            "/" + mClientList.size());
                    builder.setContentText(CURRENT_PROGRESS + "%");
                    builder.setProgress(MAX_PROGRESS, CURRENT_PROGRESS, false);
                    notificationManager.notify(0, builder.build());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e("Error: ", e.toString());
                    }
                    float individualClientProgress = pb_syncing_client.getProgress();
                    float individualClientMax = getMaxSingleSyncClientProgressBar();
                    float individualClientPercent =
                            (individualClientProgress / individualClientMax) * 100;
                    CURRENT_PROGRESS = (int) individualClientPercent;
                }

                builder.setContentTitle(mClientList.size() + " Clients Synced ");
                builder.setContentText("Sync Completed").setProgress(0, 0, false);
                notificationManager.notify(0, builder.build());
            }
        }).start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            mClientList = getArguments().getParcelableArrayList(Constants.CLIENT);
        }
        super.onCreate(savedInstanceState);
        createNotificationChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_clients, container, false);
        ButterKnife.bind(this, rootView);
        mSyncClientsDialogPresenter.attachView(this);

        showUI();

        //Start Syncing Clients
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            mSyncClientsDialogPresenter.startSyncingClients(mClientList);
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
            showProgressInNotification();
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
        mSyncClientsDialogPresenter.detachView();
    }
}
