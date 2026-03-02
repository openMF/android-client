/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.LoanAccountRejectRepository
import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.objects.account.loan.RejectLoanResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.flow.Flow

/**
 * Default implementation for [LoanAccountRejectRepository].
 */
class LoanAccountRejectRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : LoanAccountRejectRepository {

    override fun rejectLoan(
        loanId: Int,
        rejectLoanPayload: RejectLoanPayload,
    ): Flow<RejectLoanResponse> {
        return dataManagerLoan.rejectLoan(loanId, rejectLoanPayload)
    }
}
