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

import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.room.entities.group.GroupPayload
import kotlinx.coroutines.flow.Flow

interface SyncGroupPayloadsRepository {

    fun allDatabaseGroupPayload(): Flow<List<GroupPayload>>

    suspend fun createGroup(groupPayload: GroupPayload): SaveResponse

    fun deleteAndUpdateGroupPayloads(id: Int): Flow<List<GroupPayload>>

    suspend fun updateGroupPayload(groupPayload: GroupPayload)
}
