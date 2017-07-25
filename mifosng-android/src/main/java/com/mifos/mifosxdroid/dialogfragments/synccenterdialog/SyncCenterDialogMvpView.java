package com.mifos.mifosxdroid.dialogfragments.synccenterdialog;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by mayankjindal on 10/07/17.
 */

public interface SyncCenterDialogMvpView extends MvpView {

    void showUI();

    void showSyncingCenter(String centerName);

    void showSyncedFailedCenters(int failedCount);

    void setMaxSingleSyncCenterProgressBar(int total);

    void setGroupSyncProgressBarMax(int count);

    void updateGroupSyncProgressBar(int i);

    void setClientSyncProgressBarMax(int count);

    void updateClientSyncProgressBar(int i);

    void updateSingleSyncCenterProgressBar(int i);

    void updateTotalSyncCenterProgressBarAndCount(int i);

    int getMaxSingleSyncCenterProgressBar();

    void showNetworkIsNotAvailable();

    void showCentersSyncSuccessfully();

    Boolean isOnline();

    void dismissDialog();

    void showDialog();

    void hideDialog();

    void showError(int s);
}
