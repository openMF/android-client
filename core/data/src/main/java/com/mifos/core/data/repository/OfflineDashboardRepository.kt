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

import com.mifos.core.entity.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.entity.center.CenterPayload
import com.mifos.core.entity.client.ClientPayload
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.group.GroupPayload
import kotlinx.coroutines.flow.Flow
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface OfflineDashboardRepository {

    fun allDatabaseClientPayload(): Observable<List<ClientPayload>>

    fun allDatabaseGroupPayload(): Flow<List<GroupPayload>>

    fun allDatabaseCenterPayload(): Observable<List<CenterPayload>>

    fun databaseLoanRepayments(): Flow<List<LoanRepaymentRequest>>

    fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>>
}
