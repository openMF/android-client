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
import com.mifos.core.data.repository.OfflineDashboardRepository
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class OfflineDashboardRepositoryImp(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerGroups: DataManagerGroups,
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings,
) : OfflineDashboardRepository {

    override fun allDatabaseClientPayload(): Flow<DataState<List<ClientPayloadEntity>>> {
        return dataManagerClient.allDatabaseClientPayload
            .asDataStateFlow()
    }

    override fun allDatabaseGroupPayload(): Flow<DataState<List<GroupPayloadEntity>>> {
        return dataManagerGroups.allDatabaseGroupPayload
            .asDataStateFlow()
    }

    override fun allDatabaseCenterPayload(): Flow<DataState<List<CenterPayloadEntity>>> {
        return dataManagerCenter.getAllDatabaseCenterPayload
            .asDataStateFlow()
    }

    override fun databaseLoanRepayments(): Flow<DataState<List<LoanRepaymentRequestEntity>>> {
        return dataManagerLoan.databaseLoanRepayments
            .asDataStateFlow()
    }

    override fun allSavingsAccountTransactions(): Flow<DataState<List<SavingsAccountTransactionRequestEntity>>> {
        return dataManagerSavings.allSavingsAccountTransactions
            .asDataStateFlow()
    }
}
