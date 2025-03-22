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

import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface OfflineDashboardRepository {

    fun allDatabaseClientPayload(): Flow<List<com.mifos.room.entities.client.ClientPayloadEntity>>

    fun allDatabaseGroupPayload(): Flow<List<GroupPayloadEntity>>

    fun allDatabaseCenterPayload(): Flow<List<CenterPayloadEntity>>

    fun databaseLoanRepayments(): Flow<List<LoanRepaymentRequestEntity>>

    fun allSavingsAccountTransactions(): Flow<List<SavingsAccountTransactionRequestEntity>>
}
