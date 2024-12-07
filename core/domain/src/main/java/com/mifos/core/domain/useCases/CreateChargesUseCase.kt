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

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.ChargesPayload
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.objects.client.ChargeCreationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateChargesUseCase @Inject constructor(private val repository: ChargeDialogRepository) {

    suspend operator fun invoke(
        clientId: Int,
        payload: ChargesPayload,
    ): Flow<Resource<ChargeCreationResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.createCharges(clientId, payload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
