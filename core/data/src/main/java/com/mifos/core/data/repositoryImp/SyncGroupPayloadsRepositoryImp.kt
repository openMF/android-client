/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.entity.group.GroupPayload
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.responses.SaveResponse
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
