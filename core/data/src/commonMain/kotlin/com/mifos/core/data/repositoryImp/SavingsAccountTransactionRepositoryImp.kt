/*
 * Copyright 2024 Mifos Initiative
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
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountTransactionRepositoryImp(
    private val dataManagerSavings: DataManagerSavings,
) : SavingsAccountTransactionRepository {

    override fun getSavingsAccountTransactionTemplate(
        type: String,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<DataState<SavingsAccountTransactionTemplateEntity?>> {
        return dataManagerSavings.getSavingsAccountTransactionTemplate(
            type,
            savingsAccountId,
            transactionType,
        ).asDataStateFlow()
    }

    override fun processTransaction(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequestEntity,
    ): Flow<DataState<SavingsAccountTransactionResponse?>> {
        return dataManagerSavings.processTransaction(
            savingsAccountType,
            savingsAccountId,
            transactionType,
            request,
        ).asDataStateFlow()
    }

    override fun getSavingsAccountTransaction(
        savingAccountId: Int,
    ): Flow<DataState<SavingsAccountTransactionRequestEntity?>> {
        return dataManagerSavings.getSavingsAccountTransaction(savingAccountId)
            .asDataStateFlow()
    }
}
