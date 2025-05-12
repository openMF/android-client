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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow

class SyncGroupPayloadsRepositoryImp(
    private val dataManagerGroups: DataManagerGroups,
) : SyncGroupPayloadsRepository {

    override fun allDatabaseGroupPayload(): Flow<DataState<List<GroupPayloadEntity>>> {
        return dataManagerGroups.allDatabaseGroupPayload
            .asDataStateFlow()
    }

    override suspend fun createGroup(groupPayload: GroupPayloadEntity): SaveResponse {
        return dataManagerGroups.createGroup(groupPayload)
    }

    override fun deleteAndUpdateGroupPayloads(id: Int): Flow<DataState<List<GroupPayloadEntity>>> {
        return dataManagerGroups.deleteAndUpdateGroupPayloads(id)
            .asDataStateFlow()
    }

    override suspend fun updateGroupPayload(groupPayload: GroupPayloadEntity) {
        dataManagerGroups.updateGroupPayload(groupPayload)
    }
}
