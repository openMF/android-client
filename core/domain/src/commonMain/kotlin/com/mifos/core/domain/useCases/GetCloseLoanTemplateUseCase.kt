/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CloseLoanRepository
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Use case to fetch the close-loan template.
 *
 * @property repository The [CloseLoanRepository] to fetch the template.
 */
class GetCloseLoanTemplateUseCase(
    private val repository: CloseLoanRepository,
) {
    /**
     * Fetches the close-loan template for a specific loan.
     *
     * @param loanId The unique identifier of the loan account.
     * @return A [Flow] emitting the [DataState] of the template.
     */
    operator fun invoke(loanId: Int): Flow<DataState<LoanTransactionTemplate?>> {
        return repository.getCloseLoanTemplate(loanId)
    }
}
