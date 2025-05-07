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
import com.mifos.core.common.utils.Page
import com.mifos.room.entities.group.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupsListRepository {

    suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<GroupEntity>

    fun getAllLocalGroups(): Flow<DataState<Page<GroupEntity>>>
}
