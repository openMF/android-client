/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.room.entities.zipmodels.LoanAndLoanRepayment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetLoanAndLoanRepaymentUseCase(
    private val repository: SyncGroupsDialogRepository,
) {
    operator fun invoke(loanId: Int): Flow<DataState<LoanAndLoanRepayment>> =
        combine(
            repository.syncLoanById(loanId),
            repository.syncLoanRepaymentTemplate(loanId),
        ) { loan, template ->
            if (loan is DataState.Success && template is DataState.Success) {
                DataState.Success(
                    LoanAndLoanRepayment(
                        loanWithAssociations = loan.data,
                        loanRepaymentTemplate = template.data,
                    ),
                )
            } else if (loan is DataState.Error || template is DataState.Error) {
                val exception = (loan as? DataState.Error)?.exception
                    ?: (template as? DataState.Error)?.exception
                    ?: Exception("Unknown error")
                DataState.Error(exception)
            } else {
                DataState.Loading
            }
        }
}
