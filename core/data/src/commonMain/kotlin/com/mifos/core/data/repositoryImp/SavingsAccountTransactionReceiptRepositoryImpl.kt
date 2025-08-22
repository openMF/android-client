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
import com.mifos.core.data.repository.SavingsAccountTransactionReceiptRepository
import com.mifos.core.network.datamanager.DataManagerRunReport
import kotlinx.coroutines.flow.Flow

/**
 * Created by Arin Yadav on 20/08/25.
 */
class SavingsAccountTransactionReceiptRepositoryImpl(
    private val dataManagerSavings: DataManagerRunReport,
) : SavingsAccountTransactionReceiptRepository {

    override suspend fun getSavingsAccountTransactionReceipt(transactionId: Int): Flow<DataState<ByteArray>> {
        return dataManagerSavings.getSavingAccountTransactionReceipt(transactionId).asDataStateFlow()
    }
}
