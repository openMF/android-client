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

import com.mifos.core.data.repository.OfflineDashboardRepository
import com.mifos.core.entity.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.entity.client.ClientPayload
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.center.CenterPayload
import com.mifos.room.entities.group.GroupPayload
import kotlinx.coroutines.flow.Flow
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class OfflineDashboardRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerGroups: DataManagerGroups,
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings,
) : OfflineDashboardRepository {

    override fun allDatabaseClientPayload(): Observable<List<ClientPayload>> {
        return dataManagerClient.allDatabaseClientPayload
    }

    override fun allDatabaseGroupPayload(): Flow<List<GroupPayload>> {
        return dataManagerGroups.allDatabaseGroupPayload
    }

    override fun allDatabaseCenterPayload(): Flow<List<CenterPayload>> {
        return dataManagerCenter.allDatabaseCenterPayload
    }

    override fun databaseLoanRepayments(): Flow<List<LoanRepaymentRequest>> {
        return dataManagerLoan.databaseLoanRepayments
    }

    override fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.allSavingsAccountTransactions
    }
}
