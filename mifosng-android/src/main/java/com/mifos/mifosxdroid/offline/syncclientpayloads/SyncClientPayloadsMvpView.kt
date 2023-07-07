package com.mifos.mifosxdroid.offline.syncclientpayloads

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.ClientPayload

/**
 * Created by Rajan Maurya on 08/07/16.
 */
interface SyncClientPayloadsMvpView : MvpView {
    fun showPayloads(clientPayload: List<ClientPayload>)
    fun showError(stringId: Int)
    fun showSyncResponse()
    fun showClientSyncFailed(errorMessage: String)
    fun showOfflineModeDialog()
    fun showClientPayloadUpdated(clientPayload: ClientPayload)
    fun showPayloadDeletedAndUpdatePayloads(clientPayloads: List<ClientPayload>)
}