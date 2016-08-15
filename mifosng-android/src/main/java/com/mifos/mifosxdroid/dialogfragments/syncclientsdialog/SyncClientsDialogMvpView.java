package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public interface SyncClientsDialogMvpView extends MvpView {

    void showUI();

    void showSyncingClient(String clientName);

    void showSyncedFailedClients(int failedCount);

    void setMaxSingleSyncClientProgressBar(int total);

    void updateSingleSyncClientProgressBar(int i);

    void updateTotalSyncClientProgressBarAndCount(int i);

    int getMaxSingleSyncClientProgressBar();

    void showNetworkIsNotAvailable();

    void showClientsSyncSuccessfully();

    void dismissDialog();

    void showDialog();

    void hideDialog();

    void showError(int s);

}
