/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanReschedulesRepository
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

class LoanReschedulesRepositoryImpl(
    private val dataManagerLoan: DataManagerLoan,
) : LoanReschedulesRepository {

    override fun getLoanReschedules(loanId: Int): Flow<DataState<List<LoanRescheduleResponse>>> {
        return dataManagerLoan.getLoanReschedules(loanId)
            .asDataStateFlow()
    }

    override fun getLoanRescheduleTemplate(): Flow<DataState<LoanRescheduleTemplate>> {
        return dataManagerLoan.getLoanRescheduleTemplate()
            .asDataStateFlow()
    }

    override suspend fun submitLoanReschedule(request: LoanRescheduleRequest): DataState<Unit> {
        return try {
            dataManagerLoan.submitLoanReschedule(request)
            DataState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun approveReschedule(rescheduleId: Int): DataState<Unit> {
        return try {
            dataManagerLoan.approveLoanReschedule(rescheduleId)
            DataState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteReschedule(rescheduleId: Int): DataState<Unit> {
        return try {
            dataManagerLoan.rejectLoanReschedule(rescheduleId)
            DataState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
