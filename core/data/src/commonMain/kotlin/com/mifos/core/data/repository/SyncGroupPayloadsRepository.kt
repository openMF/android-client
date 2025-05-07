/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow

interface SyncGroupPayloadsRepository {

    fun allDatabaseGroupPayload(): Flow<DataState<List<GroupPayloadEntity>>>

    suspend fun createGroup(groupPayload: GroupPayloadEntity): SaveResponse

    fun deleteAndUpdateGroupPayloads(id: Int): Flow<DataState<List<GroupPayloadEntity>>>

    suspend fun updateGroupPayload(groupPayload: GroupPayloadEntity)
}
