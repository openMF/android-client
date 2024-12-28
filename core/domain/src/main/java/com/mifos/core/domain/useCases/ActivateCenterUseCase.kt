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
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.`object`.clients.ActivatePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openapitools.client.models.PostCentersCenterIdResponse
import javax.inject.Inject

class ActivateCenterUseCase @Inject constructor(private val activateRepository: ActivateRepository) {

    suspend operator fun invoke(
        centerId: Int,
        centerPayload: ActivatePayload,
    ): Flow<Resource<PostCentersCenterIdResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = activateRepository.activateCenter(centerId, centerPayload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
