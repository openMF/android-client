package com.mifos.mifosxdroid.offline.syncgrouppayloads

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.group.GroupPayload

/**
 * Created by Rajan Maurya on 19/07/16.
 */
interface SyncGroupPayloadsMvpView : MvpView {
    fun showOfflineModeDialog()
    fun showGroups(groupPayloads: List<GroupPayload>)
    fun showGroupSyncResponse()
    fun showGroupSyncFailed(errorMessage: String)
    fun showPayloadDeletedAndUpdatePayloads(groupPayloads: List<GroupPayload>)
    fun showGroupPayloadUpdated(groupPayload: GroupPayload)
    fun showError(stringId: Int)
}