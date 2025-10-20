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
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.network.datamanager.DataManagerRecurringAccount
import com.mifos.room.entities.accounts.recurring.RecurringDeposit
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.Flow

class RecurringAccountRepositoryImp(
    val dataManagerRecurringAccount: DataManagerRecurringAccount,
) : RecurringAccountRepository {
    override fun getRecuttingAccountRepository(): Flow<DataState<RecurringDepositAccountTemplate>> {
        return dataManagerRecurringAccount.getRecurringDepositAccountTemplate
            .asDataStateFlow()
    }

    override fun getRecuttingAccountRepositoryBtProduct(
        clientId: Int,
        productId: Int,
    ): Flow<DataState<RecurringDepositAccountTemplate>> {
        return dataManagerRecurringAccount.getRecurringDepositAccountTemplateByProduct(
            clientId,
            productId,
        ).asDataStateFlow()
    }

    override fun createRecurringDepositAccount(
        recurringDepositAccountPayload: RecurringDepositAccountPayload?,
    ): Flow<DataState<RecurringDeposit>> {
        return dataManagerRecurringAccount.createRecurringDepositAccount(
            recurringDepositAccountPayload,
        ).asDataStateFlow()
    }
}
