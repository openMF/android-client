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
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.payloads.ChargesPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateLoanChargesUseCase(
    private val repository: LoanChargeDialogRepository,
) {

    operator fun invoke(
        loanId: Int,
        chargesPayload: ChargesPayload,
    ): Flow<DataState<ChargeCreationResponse>> = flow {
        emit(repository.createLoanCharges(loanId, chargesPayload))
    }.asDataStateFlow()
}
