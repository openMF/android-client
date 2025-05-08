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

import androidx.paging.PagingData
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.Page
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface CenterListRepository {

    fun getAllCenters(): Flow<PagingData<CenterEntity>>

    suspend fun getCentersGroupAndMeeting(id: Int): CenterWithAssociations

    fun allDatabaseCenters(): Flow<DataState<Page<CenterEntity>>>
}
