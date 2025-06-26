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

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.data.util.extractErrorMessage
import com.mifos.core.model.objects.clients.ActivatePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ActivateGroupUseCase(
    private val activateRepository: ActivateRepository,
) {
    operator fun invoke(
        groupId: Int,
        groupPayload: ActivatePayload,
    ): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)

        try {
            val response = activateRepository.activateGroup(groupId, groupPayload)

            if (response.status.value != 200) {
                val errorMessage = extractErrorMessage(response)
                emit(DataState.Error(Exception(errorMessage), null))
            } else {
                emit(DataState.Success(Unit))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}
