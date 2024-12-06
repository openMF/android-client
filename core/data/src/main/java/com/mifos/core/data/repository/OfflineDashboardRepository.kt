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

import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.group.GroupPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface OfflineDashboardRepository {

    fun allDatabaseClientPayload(): Observable<List<ClientPayload>>

    fun allDatabaseGroupPayload(): Observable<List<GroupPayload>>

    fun allDatabaseCenterPayload(): Observable<List<CenterPayload>>

    fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>>

    fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>>
}
