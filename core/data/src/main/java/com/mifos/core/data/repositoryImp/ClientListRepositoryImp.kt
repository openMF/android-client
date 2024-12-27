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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.data.pagingSource.ClientListPagingSource
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.Client
import com.mifos.core.modelobjects.clients.Page
import kotlinx.coroutines.flow.Flow
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientListRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
) : ClientListRepository {

    override fun getAllClients(): Flow<PagingData<Client>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                ClientListPagingSource(dataManagerClient)
            },
        ).flow
    }

    override fun allDatabaseClients(): Observable<Page<Client>> {
        return dataManagerClient.allDatabaseClients
    }
}
