/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import kotlinx.coroutines.flow.Flow

interface LoanReschedulesRepository {
    fun getLoanReschedules(loanId: Int): Flow<DataState<List<LoanRescheduleResponse>>>
    fun getLoanRescheduleTemplate(): Flow<DataState<LoanRescheduleTemplate>>
    suspend fun submitLoanReschedule(request: LoanRescheduleRequest): DataState<Unit>
    suspend fun approveReschedule(rescheduleId: Int, request: LoanRescheduleApprovalRequest): DataState<Unit>
    suspend fun deleteReschedule(rescheduleId: Int, request: LoanRescheduleRejectionRequest): DataState<Unit>
}
