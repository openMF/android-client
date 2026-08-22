/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.model.ClientCloseTemplateResponse
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.network.model.CollateralItemResult
import com.mifos.core.network.model.SavingAccountOption
import com.mifos.core.network.model.StaffOption
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.ClientEntity
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface ClientDetailsRepository {

    suspend fun uploadClientImage(clientId: Int, image: MultiPartFormDataContent)

    suspend fun deleteClientImage(clientId: Int)

    suspend fun getClientAccounts(clientId: Int): ClientAccounts

    suspend fun getSavingsAccounts(clientId: Int): List<SavingAccountOption>

    suspend fun getShareAccounts(clientId: Int): List<ShareAccounts>

    suspend fun getClientStaffOptions(clientId: Int): List<StaffOption>

    suspend fun getClientCloseTemplate(): ClientCloseTemplateResponse

    suspend fun getCollateralItems(): List<CollateralItem>

    suspend fun getClientCollaterals(clientId: Int): List<CollateralItemResult>

    suspend fun getClient(clientId: Int): ClientEntity

    fun getImage(clientId: Int): Flow<String>

    suspend fun assignStaff(clientId: Int, staffId: Int): Unit

    suspend fun unassignStaff(clientId: Int, staffId: Int): Unit

    suspend fun proposeTransfer(
        clientId: Int,
        destinationOfficeId: Int,
        transferDate: String,
        note: String,
    ): Unit

    suspend fun updateDefaultSavingsAccount(clientId: Int, accountId: Long): Unit

    suspend fun closeClient(
        clientId: Int,
        closureDate: String,
        closureReasonId: Int,
    ): Unit

    suspend fun createCollateral(
        clientId: Int,
        collateralId: Int,
        quantity: String,
    ): Unit

    val clientUpdateEvents: Flow<Unit>
    suspend fun triggerClientUpdate()
}
