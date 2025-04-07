/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.room.entities.zipmodels.ClientAndClientAccounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class GetClientDetailsUseCase(
    private val repository: ClientDetailsRepository,
) {

    operator fun invoke(clientId: Int): Flow<Resource<ClientAndClientAccounts>> =
        flow {
            try {
                emit(Resource.Loading())
                val combinedFlow = combine(
                    repository.getClient(clientId),
                    repository.getClientAccounts(clientId),
                ) { client, clientAccounts ->
                    ClientAndClientAccounts(
                        client = client,
                        clienAccounts = clientAccounts,
                    )
                }.first()
                emit(Resource.Success(combinedFlow))
            } catch (exception: Exception) {
                emit(Resource.Error(MFErrorParser.errorMessage(exception)))
            }
        }
}
