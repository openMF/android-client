/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.room.entities.accounts.recurring.RecurringDeposit
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.Flow

class DataManagerRecurringAccount(
    val mBaseApiManager: BaseApiManager,
    private val prefManager: UserPreferencesRepository,
) {

    fun createRecurringDepositAccount(
        recurringDepositAccountPayload: RecurringDepositAccountPayload?,
    ): Flow<RecurringDeposit> {
        return mBaseApiManager.recurringSavingsAccountService.createRecurringDepositAccount(
            recurringDepositAccountPayload,
        )
    }

    val getRecurringDepositAccountTemplate: Flow<RecurringDepositAccountTemplate>
        get() = mBaseApiManager.recurringSavingsAccountService.getRecurringDepositAccountTemplate()

    fun getRecurringDepositAccountTemplateByProduct(
        clientId: Int,
        productId: Int,
    ): Flow<RecurringDepositAccountTemplate> {
        return mBaseApiManager.recurringSavingsAccountService.getRecurringDepositAccountTemplateByProduct(
            clientId,
            productId,
        )
    }
}
