/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanReschedulesRepository
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LoanReschedulesRepositoryImpl(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanReschedulesRepository {

    override fun getLoanReschedules(loanId: Int): Flow<DataState<List<LoanRescheduleResponse>>> {
        return dataManagerLoan.getLoanReschedules(loanId)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getLoanRescheduleTemplate(): Flow<DataState<LoanRescheduleTemplate>> {
        return dataManagerLoan.getLoanRescheduleTemplate()
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun submitLoanReschedule(request: LoanRescheduleRequest): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                dataManagerLoan.submitLoanReschedule(request)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun approveReschedule(rescheduleId: Int, request: LoanRescheduleApprovalRequest): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                dataManagerLoan.approveLoanReschedule(rescheduleId, request)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun deleteReschedule(rescheduleId: Int, request: LoanRescheduleRejectionRequest): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                dataManagerLoan.rejectLoanReschedule(rescheduleId, request)
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }
}
