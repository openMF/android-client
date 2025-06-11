/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.room.entities.zipmodels.SavingsAccountAndTransactionTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSavingsAccountAndTemplateUseCase(
    private val repository: SyncGroupsDialogRepository,
) {
    operator fun invoke(
        savingsAccountType: String,
        savingsAccountId: Int,
    ): Flow<DataState<SavingsAccountAndTransactionTemplate>> =
        combine(
            repository.syncSavingsAccount(
                savingsAccountType,
                savingsAccountId,
                Constants.TRANSACTIONS,
            ),
            repository.syncSavingsAccountTransactionTemplate(
                savingsAccountType,
                savingsAccountId,
                Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT,
            ),
        ) { savings, template ->
            if (savings is DataState.Success && template is DataState.Success) {
                DataState.Success(
                    SavingsAccountAndTransactionTemplate(
                        savingsAccountWithAssociations = savings.data,
                        savingsAccountTransactionTemplate = template.data,
                    ),
                )
            } else if (savings is DataState.Error || template is DataState.Error) {
                val exception = (savings as? DataState.Error)?.exception
                    ?: (template as? DataState.Error)?.exception
                    ?: Exception("Unknown error")
                DataState.Error(exception)
            } else {
                DataState.Loading
            }
        }
}
