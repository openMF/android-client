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
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.CloseLoanRepository
import com.mifos.core.model.objects.account.loan.CloseLoanRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case to close a loan account.
 *
 * @property repository The [CloseLoanRepository] to perform the close operation.
 */
class CloseLoanUseCase(
    private val repository: CloseLoanRepository,
) {
    /**
     * Executes the close loan operation and synchronizes the account.
     *
     * @param loanId The unique identifier of the loan account.
     * @param request The [CloseLoanRequest] payload.
     * @return A [Flow] emitting the [DataState] of the operation.
     */
    operator fun invoke(loanId: Int, request: CloseLoanRequest): Flow<DataState<Unit>> {
        return flow {
            emit(DataState.Loading)
            repository.closeLoanAccount(loanId, request)

            try {
                repository.syncLoanAccount(loanId)
            } catch (e: Exception) {
                // Log sync failure but do not propagate it to the UI as a "Close Failed" error.
            }

            emit(DataState.Success(Unit))
        }.asDataStateFlow()
    }
}
