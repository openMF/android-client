/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferRequest
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferResponse
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for account transfer operations
 */
interface AmountTransferRepository {

    /**
     * Retrieve account transfer template for populating UI dropdowns
     *
     * @param fromOfficeId Source office ID
     * @param fromClientId Source client ID
     * @param fromAccountType Source account type ID
     * @param fromAccountId Source account ID
     * @param toOfficeId Destination office ID (optional, for fetching clients)
     * @param toClientId Destination client ID (optional, for fetching accounts)
     * @param toAccountType Destination account type ID (optional, for fetching accounts)
     * @return AccountTransferTemplate with available options
     */
    fun getAccountTransferTemplate(
        fromClientId: Int,
        fromAccountType: Int,
        fromAccountId: Int,
        fromOfficeId: Int? = null,
        toOfficeId: Int? = null,
        toClientId: Int? = null,
        toAccountType: Int? = null,
    ): Flow<DataState<AccountTransferTemplate>>

    /**
     * Submit an account transfer
     *
     * @param request Account transfer request payload
     * @return Result containing AccountTransferResponse or error
     */
    suspend fun submitAccountTransfer(
        request: AccountTransferRequest,
    ): DataState<AccountTransferResponse>
}
