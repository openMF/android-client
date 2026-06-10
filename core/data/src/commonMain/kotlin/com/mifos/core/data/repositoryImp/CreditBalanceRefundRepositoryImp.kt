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
import com.mifos.core.data.mappers.loan.toDomain
import com.mifos.core.data.mappers.loan.toDto
import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundInput
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundResponse
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.LoanRefundDetails
import com.mifos.core.network.datamanager.DataManagerLoan
import template.core.base.common.manager.DispatcherManager

/**
 * Implementation of [CreditBalanceRefundRepository].
 *
 * Delegates network operations to [DataManagerLoan] and uses utilities from PR #2666:
 * - [runAsDataState] for suspend functions with network check and error handling
 * - [withNetworkCheck] for Flow-based operations with network monitoring
 */
class CreditBalanceRefundRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : CreditBalanceRefundRepository {

    /**
     * Fetches loan details required for the refund form.
     * Uses [withNetworkCheck] to ensure network availability before fetching.
     * Maps the entity to domain model [LoanRefundDetails].
     */
    override suspend fun getLoanById(loanId: Int): DataState<LoanRefundDetails?> {
        return runAsDataState(networkMonitor, dispatcher.io) {
            val entity = dataManagerLoan.getLoanRefundDetails(loanId)
            entity?.toDomain()
        }
    }

    /**
     * Submits a credit balance refund transaction.
     * Uses [runAsDataState] which:
     * - Checks network availability via [networkMonitor]
     * - Executes on [dispatcher.io]
     * - Wraps result in [DataState]
     * - Handles exceptions automatically
     *
     * Returns [DataState.Success] with Unit on successful submission.
     */
    override suspend fun submitRefund(
        loanId: Int,
        input: CreditBalanceRefundInput,
    ): DataState<CreditBalanceRefundResponse> {
        return runAsDataState(networkMonitor, dispatcher.io) {
            val responseDto = dataManagerLoan.submitCreditBalanceRefund(loanId, input.toDto())
            responseDto.toDomain()
        }
    }
}
