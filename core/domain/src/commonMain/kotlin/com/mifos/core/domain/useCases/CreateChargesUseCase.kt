/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.ChargeRepository
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.payloads.ChargesPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateChargesUseCase(
    private val repository: ChargeRepository,
) {
    operator fun invoke(
        resourceType: String,
        resourceId: Int,
        payload: ChargesPayload,
    ): Flow<DataState<ChargeCreationResponse>> = flow {
        emit(repository.createCharges(resourceType, resourceId, payload))
    }.asDataStateFlow()
}
