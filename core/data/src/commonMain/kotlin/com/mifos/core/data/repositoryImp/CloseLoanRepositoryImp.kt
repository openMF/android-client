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
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.account.loan.CloseLoanRequest
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import template.core.base.common.manager.DispatcherManager

/**
 * Default implementation of [CloseLoanRepository] backed by [DataManagerLoan].
 *
 * Aligns with the repository pattern introduced in PR #2666:
 * - Action methods are wrapped in [runAsDataState] with a network guard.
 * - Observation flows are wrapped in [withNetworkCheck] and switched onto [DispatcherManager.io].
 *
 * @property dataManagerLoan The data manager used to interact with the loan API.
 * @property networkMonitor Reactive connectivity source for network gating.
 * @property dispatcher Coroutine dispatcher provider for IO work.
 */
class CloseLoanRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : CloseLoanRepository {

    override fun getCloseLoanTemplate(loanId: Int): Flow<DataState<LoanTransactionTemplate?>> =
        networkMonitor.withNetworkCheck(
            dataManagerLoan.getLoanTransactionTemplate(loanId, "close")
                .asDataStateFlow(),
        ).flowOn(dispatcher.io)

    override suspend fun closeLoanAccount(
        loanId: Int,
        request: CloseLoanRequest,
    ): DataState<Unit> = runAsDataState(networkMonitor, dispatcher.io) {
        dataManagerLoan.closeLoanAccount(loanId, request)
        Unit
    }

    override suspend fun syncLoanAccount(loanId: Int): DataState<Unit> =
        runAsDataState(networkMonitor, dispatcher.io) {
            dataManagerLoan.syncLoanById(loanId).first()
            Unit
        }
}
