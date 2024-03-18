package com.mifos.mifosxdroid.offline.syncgrouppayloads

import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import rx.Observable
import javax.inject.Inject

class SyncGroupPayloadsRepositoryImp @Inject constructor(private val dataManagerGroups: DataManagerGroups) :
    SyncGroupPayloadsRepository {

    override fun allDatabaseGroupPayload(): Observable<List<GroupPayload>> {
        return dataManagerGroups.allDatabaseGroupPayload
    }

    override fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse> {
        return dataManagerGroups.createGroup(groupPayload)
    }

    override fun deleteAndUpdateGroupPayloads(id: Int): Observable<List<GroupPayload>> {
        return dataManagerGroups.deleteAndUpdateGroupPayloads(id)
    }

    override fun updateGroupPayload(groupPayload: GroupPayload): Observable<GroupPayload> {
        return dataManagerGroups.updateGroupPayload(groupPayload)
    }
}