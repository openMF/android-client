/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [CreditBalanceRefundRepository].
 *
 * Delegates network operations to [DataManagerLoan] and handles HTTP exception parsing
 * via [MFErrorParser].
 */
class CreditBalanceRefundRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : CreditBalanceRefundRepository {

    /**
     * Fetches loan details required for the refund form.
     * Delegates to [DataManagerLoan.getLoanById] and wraps the result in [DataState].
     */
    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId).asDataStateFlow()
    }

    /**
     * Submits a credit balance refund transaction and wraps the result in [DataState].
     * Parses HTTP exceptions using [MFErrorParser] to return readable error messages.
     */
    override suspend fun submitRefund(
        loanId: Int,
        request: CreditBalanceRefundRequest,
    ): DataState<LoanRepaymentResponseEntity> {
        return try {
            val response = dataManagerLoan.submitCreditBalanceRefund(loanId, request)
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
