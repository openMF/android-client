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

class ActivateGroupUseCase(
    private val activateRepository: ActivateRepository,
) {
    suspend operator fun invoke(
        groupId: Int,
        groupPayload: ActivatePayload,
    ): DataState<Unit> = try {
        val response = activateRepository.activateGroup(groupId, groupPayload)

        if (response.status.value != 200) {
            val errorMessage = extractErrorMessage(response)
            DataState.Error(Exception(errorMessage), null)
        } else {
            DataState.Success(Unit)
        }
    } catch (e: Exception) {
        DataState.Error(e)
    }
}
