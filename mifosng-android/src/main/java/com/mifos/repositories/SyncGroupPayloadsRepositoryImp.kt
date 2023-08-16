package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.objects.group.GroupPayload
import com.mifos.objects.response.SaveResponse
import rx.Observable
import javax.inject.Inject

class SyncGroupPayloadsRepositoryImp  @Inject constructor(private val dataManagerGroups: DataManagerGroups)  : SyncGroupPayloadsRepository {

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