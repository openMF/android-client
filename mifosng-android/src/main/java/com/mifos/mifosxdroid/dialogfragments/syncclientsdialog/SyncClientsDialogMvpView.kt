package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 08/08/16.
 */
interface SyncClientsDialogMvpView : MvpView {
    fun showUI()
    fun showSyncingClient(clientName: String?)
    fun showSyncedFailedClients(failedCount: Int)
    fun updateSingleSyncClientProgressBar(i: Int)
    fun updateTotalSyncClientProgressBarAndCount(i: Int)
    var maxSingleSyncClientProgressBar: Int
    fun showNetworkIsNotAvailable()
    fun showClientsSyncSuccessfully()
    val isOnline: Boolean?
    fun dismissDialog()
    fun showDialog()
    fun hideDialog()
    fun showError(s: Int)
}