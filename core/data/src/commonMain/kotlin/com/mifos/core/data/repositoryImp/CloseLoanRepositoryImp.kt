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
import com.mifos.core.data.repository.CloseLoanRepository
import com.mifos.core.model.objects.account.loan.CloseLoanRequest
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Default implementation of [CloseLoanRepository] backed by [DataManagerLoan].
 *
 * @property dataManagerLoan The data manager used to interact with the loan API.
 */
class CloseLoanRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : CloseLoanRepository {

    /**
     * Fetches the close-loan template from the network.
     *
     * @param loanId The unique identifier of the loan account.
     * @return A [Flow] emitting the [DataState] of the template.
     */
    override fun getCloseLoanTemplate(loanId: Int): Flow<DataState<LoanTransactionTemplate?>> {
        return dataManagerLoan.getLoanTransactionTemplate(loanId, "close")
            .asDataStateFlow()
    }

    /**
     * Submits the close loan request to the network via [DataManagerLoan].
     *
     * @param loanId The unique identifier of the loan account.
     * @param request The [CloseLoanRequest] for closing the loan.
     */
    override suspend fun closeLoanAccount(loanId: Int, request: CloseLoanRequest) {
        dataManagerLoan.closeLoanAccount(loanId, request)
    }

    /**
     * Synchronizes the loan account data by fetching it from the network and saving it locally.
     *
     * @param loanId The unique identifier of the loan account.
     */
    override suspend fun syncLoanAccount(loanId: Int) {
        dataManagerLoan.syncLoanById(loanId).first()
    }
}
