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
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow
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

    override fun allDatabaseClientPayload(): Flow<List<com.mifos.room.entities.client.ClientPayloadEntity>> {
        return dataManagerClient.allDatabaseClientPayload
    }

    override fun allDatabaseGroupPayload(): Flow<List<GroupPayloadEntity>> {
        return dataManagerGroups.allDatabaseGroupPayload
    }

    override fun allDatabaseCenterPayload(): Flow<List<CenterPayloadEntity>> {
        return dataManagerCenter.allDatabaseCenterPayload
    }

    override fun databaseLoanRepayments(): Flow<List<LoanRepaymentRequestEntity>> {
        return dataManagerLoan.databaseLoanRepayments
    }

    override fun allSavingsAccountTransactions(): Flow<List<SavingsAccountTransactionRequestEntity>> {
        return dataManagerSavings.allSavingsAccountTransactions
    }
}
