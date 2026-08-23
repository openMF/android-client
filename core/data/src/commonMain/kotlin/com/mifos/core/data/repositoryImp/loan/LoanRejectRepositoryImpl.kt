/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp.loan

import com.mifos.core.data.repository.LoanRejectRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runSuspendCall
import com.mifos.core.model.objects.account.loan.RejectLoanInput
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.dto.loans.RejectLoanRequestDto
import kpt.core.base.common.manager.DispatcherManager

class LoanRejectRepositoryImpl(
    private val dataManagerLoan: DataManagerLoan,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : LoanRejectRepository {
    override suspend fun rejectLoan(
        loanId: Int,
        request: RejectLoanInput,
    ): Unit {
        return runSuspendCall(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            dataManagerLoan.rejectLoan(
                loanId = loanId,
                request = RejectLoanRequestDto(
                    rejectedOnDate = request.rejectedOnDate,
                    note = request.note,
                    locale = request.locale,
                    dateFormat = request.dateFormat,
                ),
            )
        }
    }
}
