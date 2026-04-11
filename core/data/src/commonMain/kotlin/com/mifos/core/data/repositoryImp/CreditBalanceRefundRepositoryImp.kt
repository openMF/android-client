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
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.account.loan.LoanRefundDetails
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Implementation of [CreditBalanceRefundRepository].
 *
 * Delegates network operations to [DataManagerLoan] and handles HTTP exception parsing
 * via [MFErrorParser].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreditBalanceRefundRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val networkMonitor: NetworkMonitor,
) : CreditBalanceRefundRepository {

    /**
     * Internal event bus: emits [Unit] whenever a refund is successfully submitted.
     * [getLoanById] combines this trigger to automatically re-fetch the latest loan data.
     */
    private val _updateTrigger = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val updateTrigger: Flow<Unit> = _updateTrigger.asSharedFlow()

    /**
     * Fetches loan details required for the refund form.
     * Delegates to [DataManagerLoan.getLoanById] and wraps the result in [DataState].
     * Combines network status and refresh triggers to provide the latest data.
     */
    override fun getLoanById(loanId: Int): Flow<DataState<LoanRefundDetails?>> {
        return combine(
            networkMonitor.isOnline,
            _updateTrigger.onStart { emit(Unit) },
        ) { isOnline, _ ->
            isOnline
        }.flatMapLatest { isOnline ->
            if (isOnline) {
                dataManagerLoan.getLoanById(loanId)
                    .map { entity ->
                        entity?.let {
                            LoanRefundDetails(
                                id = it.id,
                                accountNo = it.accountNo,
                                clientName = it.clientName,
                                totalOverpaid = it.totalOverpaid,
                                currencyCode = it.currency.code,
                                decimalPlaces = it.currency.decimalPlaces,
                            )
                        }
                    }.asDataStateFlow()
            } else {
                flowOf(DataState.Error(Exception("Network not available")))
            }
        }
    }

    /**
     * Submits a credit balance refund transaction and wraps the result in [DataState].
     * Parses HTTP exceptions using [MFErrorParser] to return readable error messages.
     * Triggers a loan update event upon successful submission.
     */
    override suspend fun submitRefund(
        loanId: Int,
        request: CreditBalanceRefundRequest,
    ): DataState<LoanRepaymentResponseEntity> {
        if (!networkMonitor.isOnline.first()) {
            return DataState.Error(Exception("Network not available"))
        }
        return try {
            val response = dataManagerLoan.submitCreditBalanceRefund(loanId, request)
            _updateTrigger.emit(Unit) // refreshes this screen's getLoanById flow
            DataState.Success(response)
        } catch (e: ClientRequestException) {
            DataState.Error(Exception(MFErrorParser.errorMessage(e)))
        } catch (e: ServerResponseException) {
            DataState.Error(Exception(MFErrorParser.errorMessage(e)))
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
