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
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Use case to calculate loan repayment schedule without creating the loan.
 * This is used to preview the schedule before submitting the loan application.
 */
class CalculateLoanScheduleUseCase(
    private val loanAccountRepository: LoanAccountRepository,
) {

    operator fun invoke(loansPayload: LoansPayload): Flow<DataState<LoanWithAssociationsEntity>> =
        loanAccountRepository.calculateLoanSchedule(loansPayload)
}
