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
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 04/08/2024 (12:33 PM)
 */
class ActivateSavingsUseCase(
    private val repository: SavingsAccountActivateRepository,
) {

    operator fun invoke(
        savingsAccountId: Int,
        request: HashMap<String, String>,
    ): Flow<DataState<GenericResponse>> =
        repository.activateSavings(savingsAccountId, request)
}
