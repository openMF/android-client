/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface OfflineDashboardRepository {

    fun allDatabaseClientPayload(): Flow<DataState<List<ClientPayloadEntity>>>

    fun allDatabaseGroupPayload(): Flow<DataState<List<GroupPayloadEntity>>>

    fun allDatabaseCenterPayload(): Flow<DataState<List<CenterPayloadEntity>>>

    fun databaseLoanRepayments(): Flow<DataState<List<LoanRepaymentRequestEntity>>>

    fun allSavingsAccountTransactions(): Flow<DataState<List<SavingsAccountTransactionRequestEntity>>>
}
