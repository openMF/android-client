/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.account.loan.transfer.AccountTransferRequest
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferResponse
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Data manager for account transfer operations
 * Delegates to DataManagerLoan for API calls
 */
class DataManagerAccountTransfer(
    private val dataManagerLoan: DataManagerLoan,
) {

    /**
     * Retrieve account transfer template for populating UI dropdowns
     */
    fun getAccountTransferTemplate(
        fromOfficeId: Int,
        fromClientId: Int,
        fromAccountType: Int,
        fromAccountId: Int,
    ): Flow<AccountTransferTemplate> {
        return dataManagerLoan.getAccountTransferTemplate(
            fromOfficeId = fromOfficeId,
            fromClientId = fromClientId,
            fromAccountType = fromAccountType,
            fromAccountId = fromAccountId,
        )
    }

    /**
     * Submit an account transfer
     */
    suspend fun submitAccountTransfer(
        request: AccountTransferRequest,
    ): AccountTransferResponse {
        return dataManagerLoan.submitAccountTransfer(request)
    }
}
