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

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.mappers.loan.toDomain
import com.mifos.core.data.mappers.loan.toDto
import com.mifos.core.data.repository.loan.LoanOfficerRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerInput
import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerResponse
import com.mifos.core.model.objects.template.loan.LoanOfficerOption
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.CoroutineDispatcher

class LoanOfficerRepositoryImpl(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
) : LoanOfficerRepository {

    override suspend fun getLoanOfficerOptions(loanId: Int): DataState<List<LoanOfficerOption>> {
        return runAsDataState(
            networkMonitor,
            ioDispatcher,
        ) {
            dataManagerLoan.getLoanOfficerTemplate(loanId).toDomain()
        }
    }

    override suspend fun assignLoanOfficer(
        loanId: Int,
        input: AssignLoanOfficerInput,
    ): DataState<AssignLoanOfficerResponse> {
        return runAsDataState(
            networkMonitor,
            ioDispatcher,
        ) {
            dataManagerLoan.assignLoanOfficer(loanId, input.toDto()).toDomain()
        }
    }
}
