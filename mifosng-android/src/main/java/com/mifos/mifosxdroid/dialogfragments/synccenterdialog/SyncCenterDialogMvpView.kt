package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by mayankjindal on 10/07/17.
 */
interface SyncCenterDialogMvpView : MvpView {
    fun showUI()
    fun showSyncingCenter(centerName: String)
    fun showSyncedFailedCenters(failedCount: Int)
    fun setGroupSyncProgressBarMax(count: Int)
    fun updateGroupSyncProgressBar(index: Int)
    fun setClientSyncProgressBarMax(count: Int)
    fun updateClientSyncProgressBar(index: Int)
    fun updateSingleSyncCenterProgressBar(count: Int)
    fun updateTotalSyncCenterProgressBarAndCount(index: Int)
    var maxSingleSyncCenterProgressBar: Int
    fun showNetworkIsNotAvailable()
    fun showCentersSyncSuccessfully()
    val isOnline: Boolean?
    fun dismissDialog()
    fun showDialog()
    fun hideDialog()
    fun showError(s: Int)
}