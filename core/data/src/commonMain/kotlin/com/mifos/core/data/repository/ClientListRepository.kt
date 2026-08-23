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
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kpt.core.base.store.paging.PagingScreenStream

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientListRepository {

    /**
     * Offline-first paged client-list stream — the native Store5 paging idiom that
     * replaces the dual online-Paging3 / offline-DB read paths. Backed by
     * `Store<PageKey, List<ClientEntity>>.asPagingScreenStream(...)`.
     */
    fun clientListPagingStream(
        scope: CoroutineScope,
        pageSize: Int = 10,
    ): PagingScreenStream<ClientEntity>

    fun getAllClients(): Flow<PagingData<ClientEntity>>

    fun allDatabaseClients(): Flow<Page<ClientEntity>>
}
