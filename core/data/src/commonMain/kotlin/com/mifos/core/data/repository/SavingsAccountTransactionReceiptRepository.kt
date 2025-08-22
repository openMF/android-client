/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import kotlinx.coroutines.flow.Flow

/**
 * Created by Arin Yadav on 20/08/25.
 */
interface SavingsAccountTransactionReceiptRepository {

    suspend fun getSavingsAccountTransactionReceipt(
        transactionId: Int,
    ): Flow<DataState<ByteArray>>
}
