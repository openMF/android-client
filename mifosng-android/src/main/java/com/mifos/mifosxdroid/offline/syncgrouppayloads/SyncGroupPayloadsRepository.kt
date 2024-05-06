package com.mifos.mifosxdroid.offline.syncgrouppayloads

import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import rx.Observable

interface SyncGroupPayloadsRepository {

    fun allDatabaseGroupPayload(): Observable<List<GroupPayload>>

    fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse>

    fun deleteAndUpdateGroupPayloads(id: Int): Observable<List<GroupPayload>>

    fun updateGroupPayload(groupPayload: GroupPayload): Observable<GroupPayload>
}