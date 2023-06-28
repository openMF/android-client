package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 11/09/16.
 */
interface SyncGroupsDialogMvpView : MvpView {
    fun showUI()
    fun showSyncingGroup(clientName: String?)
    fun showSyncedFailedGroups(failedCount: Int)
    fun setClientSyncProgressBarMax(count: Int)
    fun updateClientSyncProgressBar(i: Int)
    fun updateSingleSyncGroupProgressBar(i: Int)
    fun updateTotalSyncGroupProgressBarAndCount(i: Int)
    var maxSingleSyncGroupProgressBar: Int
    fun showNetworkIsNotAvailable()
    fun showGroupsSyncSuccessfully()
    val isOnline: Boolean?
    fun dismissDialog()
    fun showDialog()
    fun hideDialog()
    fun showError(s: Int)
}