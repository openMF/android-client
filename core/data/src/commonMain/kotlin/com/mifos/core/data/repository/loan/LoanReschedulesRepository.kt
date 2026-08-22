/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository.loan

import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import kotlinx.coroutines.flow.Flow

interface LoanReschedulesRepository {
    fun getLoanReschedules(loanId: Int): Flow<List<LoanRescheduleResponse>>
    fun getLoanRescheduleTemplate(): Flow<LoanRescheduleTemplate>
    suspend fun submitLoanReschedule(request: LoanRescheduleRequest): Unit
    suspend fun approveReschedule(rescheduleId: Int, request: LoanRescheduleApprovalRequest): Unit
    suspend fun deleteReschedule(rescheduleId: Int, request: LoanRescheduleRejectionRequest): Unit
}
