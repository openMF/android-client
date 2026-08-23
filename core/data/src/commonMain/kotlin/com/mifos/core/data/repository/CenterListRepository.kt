/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import androidx.paging.PagingData
import com.mifos.core.common.utils.Page
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kpt.core.base.store.paging.PagingScreenStream

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface CenterListRepository {

    /**
     * Offline-first paged center-list stream — the native Store5 paging idiom that
     * replaces the online-Paging3 read path (`CenterListPagingSource`). Backed by
     * `Store<PageKey, List<CenterEntity>>.asPagingScreenStream(...)`.
     */
    fun centerListPagingStream(
        scope: CoroutineScope,
        pageSize: Int = 10,
    ): PagingScreenStream<CenterEntity>

    fun getAllCenters(): Flow<PagingData<CenterEntity>>

    suspend fun getCentersGroupAndMeeting(id: Int): CenterWithAssociations

    fun allDatabaseCenters(): Flow<Page<CenterEntity>>
}
