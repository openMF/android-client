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
import com.mifos.room.entities.center.CenterPayloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCenterPayloadsRepository {

    fun getAllDatabaseCenterPayload(): Flow<DataState<List<CenterPayloadEntity>>>

    suspend fun createCenter(centerPayload: CenterPayloadEntity?)

    fun deleteAndUpdateCenterPayloads(id: Int): Flow<DataState<List<CenterPayloadEntity>>>

    suspend fun updateCenterPayload(centerPayload: CenterPayloadEntity)
}
