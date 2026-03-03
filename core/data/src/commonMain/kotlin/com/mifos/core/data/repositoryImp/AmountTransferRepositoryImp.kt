/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.AmountTransferRepository
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferRequest
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferResponse
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferTemplate
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.flow.Flow

/**
 * Repository implementation for account transfer operations
 */
class AmountTransferRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : AmountTransferRepository {

    override fun getAccountTransferTemplate(
        fromClientId: Int,
        fromAccountType: Int,
        fromAccountId: Int,
        fromOfficeId: Int?,
        toOfficeId: Int?,
        toClientId: Int?,
        toAccountType: Int?,
    ): Flow<DataState<AccountTransferTemplate>> {
        return dataManagerLoan.getAccountTransferTemplate(
            fromOfficeId = fromOfficeId,
            fromClientId = fromClientId,
            fromAccountType = fromAccountType,
            fromAccountId = fromAccountId,
            toOfficeId = toOfficeId,
            toClientId = toClientId,
            toAccountType = toAccountType,
        ).asDataStateFlow()
    }

    override suspend fun submitAccountTransfer(
        request: AccountTransferRequest,
    ): DataState<AccountTransferResponse> {
        return try {
            val response = dataManagerLoan.submitAccountTransfer(request)
            DataState.Success(response)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
