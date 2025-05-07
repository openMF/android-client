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
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingsAccountSummaryRepositoryImp(
    private val dataManagerSavings: DataManagerSavings,
) : SavingsAccountSummaryRepository {

    override fun getSavingsAccount(
        type: String,
        savingsAccountId: Int,
        association: String?,
    ): Flow<DataState<SavingsAccountWithAssociationsEntity?>> {
        return dataManagerSavings.getSavingsAccount(type, savingsAccountId, association)
            .asDataStateFlow()
    }
}
