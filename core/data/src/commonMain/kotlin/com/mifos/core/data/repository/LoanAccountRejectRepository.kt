/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.objects.account.loan.RejectLoanResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for rejecting loan applications.
 */
interface LoanAccountRejectRepository {

    /**
     * Rejects a submitted and pending loan.
     */
    fun rejectLoan(
        loanId: Int,
        rejectLoanPayload: RejectLoanPayload,
    ): Flow<RejectLoanResponse>
}
