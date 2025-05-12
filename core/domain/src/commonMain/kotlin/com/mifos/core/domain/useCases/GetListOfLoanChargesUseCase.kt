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
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow

class GetListOfLoanChargesUseCase(
    private val repository: LoanChargeRepository,
) {

    operator fun invoke(loanId: Int): Flow<DataState<List<ChargesEntity>>> =
        repository.getListOfLoanCharges(loanId)
}
