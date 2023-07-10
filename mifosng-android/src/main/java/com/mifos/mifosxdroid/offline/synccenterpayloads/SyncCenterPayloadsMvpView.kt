package com.mifos.mifosxdroid.offline.synccenterpayloads

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.services.data.CenterPayload

/**
 * Created by mayankjindal on 04/07/17.
 */
interface SyncCenterPayloadsMvpView : MvpView {
    fun showOfflineModeDialog()
    fun showCenters(centerPayloads: List<CenterPayload>)
    fun showCenterSyncResponse()
    fun showCenterSyncFailed(errorMessage: String)
    fun showPayloadDeletedAndUpdatePayloads(centerPayloads: List<CenterPayload>)
    fun showCenterPayloadUpdated(centerPayload: CenterPayload)
    fun showError(stringId: Int)
}