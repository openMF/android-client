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

import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.entity.client.Client
import com.mifos.core.entity.client.ClientPayload
import com.mifos.core.network.datamanager.DataManagerClient
import rx.Observable
import javax.inject.Inject

class SyncClientPayloadsRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    SyncClientPayloadsRepository {

    override fun allDatabaseClientPayload(): Observable<List<ClientPayload>> {
        return dataManagerClient.allDatabaseClientPayload
    }

    override fun createClient(clientPayload: ClientPayload): Observable<Client> {
        return dataManagerClient.createClient(clientPayload)
    }

    override fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long,
    ): Observable<List<ClientPayload>> {
        return dataManagerClient.deleteAndUpdatePayloads(id, clientCreationTIme)
    }

    override fun updateClientPayload(clientPayload: ClientPayload): Observable<ClientPayload> {
        return dataManagerClient.updateClientPayload(clientPayload)
    }
}
