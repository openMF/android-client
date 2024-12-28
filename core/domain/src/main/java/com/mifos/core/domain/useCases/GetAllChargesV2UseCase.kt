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
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.`object`.template.client.ChargeTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllChargesV2UseCase @Inject constructor(private val repository: ChargeDialogRepository) {

    suspend operator fun invoke(clientId: Int): Flow<Resource<ChargeTemplate>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getAllChargesV2(clientId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
