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
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.extractErrorMessage
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.model.SavingAccountOption
import com.mifos.core.network.model.StaffOption
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.ClientEntity
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsRepositoryImp(
    private val dataManagerClient: DataManagerClient,
) : ClientDetailsRepository {

    override suspend fun uploadClientImage(clientId: Int, image: MultiPartFormDataContent) {
        dataManagerClient.uploadClientImage(clientId, image)
    }

    override suspend fun deleteClientImage(clientId: Int) {
        dataManagerClient.deleteClientImage(clientId)
    }

    override suspend fun getClientAccounts(clientId: Int): ClientAccounts {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override suspend fun getClientStaffOptions(clientId: Int): List<StaffOption> {
        return dataManagerClient.getClientStaff(clientId)
    }

    override suspend fun getSavingsAccounts(clientId: Int): List<SavingAccountOption> {
        return dataManagerClient.getSavingsAccounts(clientId)
    }

    override suspend fun getClient(clientId: Int): ClientEntity {
        return dataManagerClient.getClient(clientId)
    }

    override fun getImage(clientId: Int): Flow<DataState<String>> {
        return dataManagerClient.getClientImage(clientId)
    }

    override suspend fun assignStaff(
        clientId: Int,
        staffId: Int,
    ): DataState<Unit> {
        return try {
            val res = dataManagerClient.assignClientStaff(clientId, staffId)
            if (res.status.value == 200) {
                DataState.Success(Unit)
            } else {
                val errorBody = extractErrorMessage(res)
                DataState.Error(Exception(errorBody))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun unassignStaff(
        clientId: Int,
        staffId: Int,
    ): DataState<Unit> {
        return try {
            val res = dataManagerClient.unAssignClientStaff(clientId, staffId)
            if (res.status.value == 200) {
                DataState.Success(Unit)
            } else {
                val errorBody = extractErrorMessage(res)
                DataState.Error(Exception(errorBody))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun proposeTransfer(
        clientId: Int,
        destinationOfficeId: Int,
        transferDate: String,
        note: String,
    ): DataState<Unit> {
        return try {
            val res = dataManagerClient.proposeClientTransfer(
                clientId = clientId,
                destinationOfficeId = destinationOfficeId,
                transferDate = transferDate,
                note = note,
            )
            if (res.status.value == 200) {
                DataState.Success(Unit)
            } else {
                val errorBody = extractErrorMessage(res)
                DataState.Error(Exception(errorBody))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateDefaultSavingsAccount(
        clientId: Int,
        accountId: Long,
    ): DataState<Unit> {
        return try {
            val res = dataManagerClient.updateDefaultSavingsAccount(
                clientId = clientId,
                savingsId = accountId,
            )
            if (res.status.value == 200) {
                DataState.Success(Unit)
            } else {
                val errorBody = extractErrorMessage(res)
                DataState.Error(Exception(errorBody))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
