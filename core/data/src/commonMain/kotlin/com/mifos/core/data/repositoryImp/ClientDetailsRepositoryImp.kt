/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.model.ClientCloseTemplateResponse
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.network.model.CollateralItemResult
import com.mifos.core.network.model.SavingAccountOption
import com.mifos.core.network.model.StaffOption
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.ClientEntity
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Created by Aditya Gupta on 06/08/23.
 *
 * `getClient` is served through the Store5 [ClientStore][com.mifos.core.store.provideClientStore]
 * (qualifier [AppStoreRegistry.Clients][kpt.core.store.AppStoreRegistry.Clients]) instead of a raw
 * `DataManagerClient.getClient` network call. `StoreReadRequest.cached(refresh = true)` emits the
 * Room-persisted client immediately (so the details screen renders offline from cache) AND triggers
 * a background network refresh when connectivity is available (SWR). The `suspend fun ... :
 * ClientEntity` interface shape is preserved (Option B) — 4 callers across the loan + client
 * features consume it as a plain suspend value, so widening it to a `Flow` would ripple far — and
 * the store's cached-then-fresh stream is bridged back to a single value with `.first()` over the
 * Data responses. Every other method here (uploadClientImage, assignStaff, closeClient, …) is a
 * WRITE op and continues to hit [dataManagerClient] unchanged.
 */
class ClientDetailsRepositoryImp(
    private val dataManagerClient: DataManagerClient,
    private val clientStore: Store<Int, ClientEntity>,
) : ClientDetailsRepository {

    private val _clientUpdateEvents = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val clientUpdateEvents: Flow<Unit> = _clientUpdateEvents.asSharedFlow()

    override suspend fun triggerClientUpdate() {
        _clientUpdateEvents.emit(Unit)
    }

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

    override suspend fun getShareAccounts(clientId: Int): List<ShareAccounts> {
        return dataManagerClient.getClientAccounts(clientId).shareAccounts
    }

    override suspend fun getClientCloseTemplate(): ClientCloseTemplateResponse {
        return dataManagerClient.getClientCloseTemplate()
    }

    override suspend fun getCollateralItems(): List<CollateralItem> {
        return dataManagerClient.getCollateralItems()
    }

    override suspend fun getClientCollaterals(clientId: Int): List<CollateralItemResult> {
        return dataManagerClient.getClientCollateralItems(clientId)
    }

    override suspend fun getClient(clientId: Int): ClientEntity {
        // Offline-first read: stream the Store5 client store cache-first (refresh in background)
        // and return the first Data value. With a previously-cached client the SoT reader emits
        // the Room row immediately, so the details screen renders offline from cache instead of
        // erroring. The group-name back-fill that used to live here now runs inside the store's
        // fetcher (see provideClientStore#resolveGroup), so the persisted row already carries it.
        return clientStore
            .stream(StoreReadRequest.cached(key = clientId, refresh = true))
            .filterIsInstance<StoreReadResponse.Data<ClientEntity>>()
            .map { it.value }
            .first()
    }

    override fun getImage(clientId: Int): Flow<String> {
        return dataManagerClient.getClientImage(clientId)
    }

    override suspend fun assignStaff(
        clientId: Int,
        staffId: Int,
    ) {
        val res = dataManagerClient.assignClientStaff(clientId, staffId)
        if (res.status.value != 200) {
            throw Exception(extractErrorMessage(res))
        }
    }

    override suspend fun unassignStaff(
        clientId: Int,
        staffId: Int,
    ) {
        val res = dataManagerClient.unAssignClientStaff(clientId, staffId)
        if (res.status.value != 200) {
            throw Exception(extractErrorMessage(res))
        }
    }

    override suspend fun proposeTransfer(
        clientId: Int,
        destinationOfficeId: Int,
        transferDate: String,
        note: String,
    ) {
        val res = dataManagerClient.proposeClientTransfer(
            clientId = clientId,
            destinationOfficeId = destinationOfficeId,
            transferDate = transferDate,
            note = note,
        )
        if (res.status.value != 200) {
            throw Exception(extractErrorMessage(res))
        }
    }

    override suspend fun updateDefaultSavingsAccount(
        clientId: Int,
        accountId: Long,
    ) {
        val res = dataManagerClient.updateDefaultSavingsAccount(
            clientId = clientId,
            savingsId = accountId,
        )
        if (res.status.value != 200) {
            throw Exception(extractErrorMessage(res))
        }
    }

    override suspend fun closeClient(
        clientId: Int,
        closureDate: String,
        closureReasonId: Int,
    ) {
        val res = dataManagerClient.closeClient(
            clientId = clientId,
            closureDate = closureDate,
            closureReasonId = closureReasonId,
        )
        if (res.status.value != 200) {
            throw Exception(extractErrorMessage(res))
        }
    }

    override suspend fun createCollateral(
        clientId: Int,
        collateralId: Int,
        quantity: String,
    ) {
        val res = dataManagerClient.createCollateral(
            clientId = clientId,
            collateralId = collateralId,
            quantity = quantity,
        )
        if (res.status.value != 200) {
            throw Exception(extractErrorMessage(res))
        }
    }
}
