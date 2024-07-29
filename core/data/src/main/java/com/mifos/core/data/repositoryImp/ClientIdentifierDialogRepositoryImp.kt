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

import com.mifos.core.data.repository.ClientIdentifierDialogRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class ClientIdentifierDialogRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientIdentifierDialogRepository {

    override fun getClientIdentifierTemplate(clientId: Int): Observable<IdentifierTemplate> {
        return dataManagerClient.getClientIdentifierTemplate(clientId)
    }

    override suspend fun createClientIdentifier(
        clientId: Int,
        identifierPayload: IdentifierPayload,
    ): IdentifierCreationResponse {
        return dataManagerClient.createClientIdentifier(clientId, identifierPayload)
    }
}
