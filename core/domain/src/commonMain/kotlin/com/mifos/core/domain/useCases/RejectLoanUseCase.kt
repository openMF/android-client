/*
 * Copyright 2026 Mifos Initiative
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
import com.mifos.core.data.repository.LoanAccountRejectRepository
import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.objects.account.loan.RejectLoanResponse
import kotlinx.coroutines.flow.Flow

/**
 * Use case for rejecting a loan application.
 */
class RejectLoanUseCase(
    private val repository: LoanAccountRejectRepository,
) {

    /**
     * Reject a loan and expose the request state as [DataState].
     */
    operator fun invoke(
        loanId: Int,
        rejectLoanPayload: RejectLoanPayload,
    ): Flow<DataState<RejectLoanResponse>> {
        return repository.rejectLoan(loanId, rejectLoanPayload)
            .asDataStateFlow()
    }
}
