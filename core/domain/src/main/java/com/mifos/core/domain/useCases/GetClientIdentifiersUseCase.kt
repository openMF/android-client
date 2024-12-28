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
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.`object`.noncoreobjects.Identifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetClientIdentifiersUseCase @Inject constructor(private val clientIdentifiersRepository: ClientIdentifiersRepository) {

    suspend operator fun invoke(clientId: Int): Flow<Resource<List<Identifier>>> = flow {
        try {
            emit(Resource.Loading())
            val response = clientIdentifiersRepository.getClientIdentifiers(clientId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
