/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.Page
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.model.objects.clients.AssignStaffRequest
import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import com.mifos.core.model.objects.clients.ClientCloseRequest
import com.mifos.core.model.objects.clients.CollateralPayload
import com.mifos.core.model.objects.clients.ProposeTransferRequest
import com.mifos.core.model.objects.clients.UpdateSavingsAccountRequest
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.clients.GetClientResponseMapper
import com.mifos.core.network.mappers.clients.GetClientsClientIdAccountMapper
import com.mifos.core.network.model.ClientCloseTemplateResponse
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.network.model.PinpointLocationActionResponse
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.network.model.PostClientAddressResponse
import com.mifos.core.network.model.PostClientsClientIdRequest
import com.mifos.core.network.model.PostClientsClientIdResponse
import com.mifos.core.network.model.SavingAccountOption
import com.mifos.core.network.model.StaffOption
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.AddressConfiguration
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.helper.ClientDaoHelper
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * This DataManager is for Managing Client API, In which Request is going to Server
 * and In Response, We are getting Client API Observable Response using Retrofit2 .
 * Created by Rajan Maurya on 24/06/16.
 */
class DataManagerClient(
    val mBaseApiManager: BaseApiManager,
//    private val mDatabaseHelperClient: DatabaseHelperClient,
    private val clientDatabaseHelper: ClientDaoHelper,
    private val prefManager: UserPreferencesRepository,
) {
    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the clients. The response is pass to the DatabaseHelperClient
     * that save the response in Database different thread and next pass the response to
     * Presenter to show in the view
     *
     *
     * If the offset is zero and UserStatus is 1 then fetch all clients list and show on the view.
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the client list REST API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum Number of clients will come in response
     * @return Client List from offset to max Limit
     */
    suspend fun getAllClients(offset: Int, limit: Int): Page<ClientEntity> {
        return mBaseApiManager.clientsApi.retrieveAll21(
            null, null, null,
            null, null, null,
            null, offset,
            limit, null, null, null,
        ).let(GetClientResponseMapper::mapFromEntity)
    }
//    fun getAllClients(offset: Int, limit: Int): Observable<Page<Client>> {
//        return when (prefManager.userStatus) {
//            false -> baseApiManager.getClientsApi().retrieveAll21(
//                null, null, null,
//                null, null, null,
//                null, null, offset,
//                limit, null, null, null
//            ).map(GetClientResponseMapper::mapFromEntity)
//
//            true -> {
//                /**
//                 * Return All Clients List from DatabaseHelperClient only one time.
//                 * If offset is zero this means this is first request and
//                 * return all clients from DatabaseHelperClient
//                 */
//                if (offset == 0) mDatabaseHelperClient.readAllClients() else Observable.just(Page())
//            }
//        }
//    }

    /**
     * This Method Request to the DatabaseHelperClient and DatabaseHelperClient Read the All
     * clients from Client_Table and give the response Page of List of Client
     *
     * @return Page of Client List
     */
    val allDatabaseClients: Flow<Page<ClientEntity>>
        get() = clientDatabaseHelper.readAllClients()

    /**
     * This Method
     *
     * @param clientId for Query in database or REST API request.
     * @return The Client Details
     */
    suspend fun getClient(clientId: Int): ClientEntity {
        return mBaseApiManager.clientService.getClient(clientId)
    }
//    fun getClient(clientId: Int): Observable<Client> {
//        return when (prefManager.userStatus) {
//            false -> mBaseApiManager.clientsApi.getClient(clientId)
//                .concatMap { client -> Observable.just(client) }
//
//            true ->
//                /**
//                 * Return Clients from DatabaseHelperClient only one time.
//                 */
//                mDatabaseHelperClient.getClient(clientId)
//        }
//    }

    suspend fun syncClientInDatabase(client: ClientEntity) {
        clientDatabaseHelper.saveClient(client)
    }

    /**
     * This Method Checks the User Status and as accordingly call to Database Helper or Client
     * Retrofit Service.
     * If User is Online then this Client DataManager Request to Client Retrofit to make REST API
     * Request for Client Account.
     * If User if Offline Client DataManager request to Client DatabaseHelper to make Transaction
     * for retrieving the Clients Account.
     *
     * @param clientId Client Id
     * @return All Clients Account, Like Savings, Loan etc Accounts.
     */
    suspend fun getClientAccounts(clientId: Int): ClientAccounts {
        return mBaseApiManager.clientsApi.retrieveAssociatedAccounts(clientId.toLong())
            .let(GetClientsClientIdAccountMapper::mapFromEntity)
    }
//    fun getClientAccounts(clientId: Int): Observable<ClientAccounts> {
//        return when (prefManager.userStatus) {
//            false -> baseApiManager.getClientsApi().retrieveAssociatedAccounts(clientId.toLong())
//                .map(GetClientsClientIdAccountMapper::mapFromEntity)
//
//            true ->
//                /**
//                 * Return Clients from DatabaseHelperClient only one time.
//                 */
//                mDatabaseHelperClient.realClientAccounts(clientId)
//        }
//    }

    suspend fun getClientStaff(clientId: Int): List<StaffOption> {
        return mBaseApiManager.clientService.getClientTemplate(clientId).staffOptions
    }

    suspend fun getSavingsAccounts(clientId: Int): List<SavingAccountOption> {
        return mBaseApiManager.clientService.getClientTemplate(clientId).savingAccountOptions
    }

    suspend fun getClientCloseTemplate(): ClientCloseTemplateResponse {
        return mBaseApiManager.clientService.getClientCloseTemplate()
    }

    suspend fun getCollateralItems(): List<CollateralItem> {
        return mBaseApiManager.clientService.getCollateralItems()
    }

    /**
     * This Method Fetching the Client Accounts (Loan, saving, etc Accounts ) from REST API
     * and get the ClientAccounts and then Saving all Accounts into the Database and give the
     * ClientAccount in return.
     *
     * @param clientId Client Id
     * @return ClientAccounts
     */
    suspend fun syncClientAccounts(clientId: Int): ClientAccounts {
        return mBaseApiManager.clientsApi.retrieveAssociatedAccounts(clientId.toLong())
            .let(GetClientsClientIdAccountMapper::mapFromEntity)
    }
//    fun syncClientAccounts(clientId: Int): Observable<ClientAccounts> {
//        return baseApiManager.getClientsApi().retrieveAssociatedAccounts(clientId.toLong())
//            .map(GetClientsClientIdAccountMapper::mapFromEntity)
//            .concatMap { clientAccounts ->
//                mDatabaseHelperClient.saveClientAccounts(
//                    clientAccounts,
//                    clientId
//                )
//            }
//    }

    /**
     * This Method for removing the Client Image from his profile on server
     * if its response is true the client does not have any profile Image and if
     * response is false then failed to update the client image from server profile.
     * There can any problem during updating the client image like Network error.
     *
     * @param clientId Client ID
     * @return ResponseBody is the Retrofit 2 response
     */
    suspend fun deleteClientImage(clientId: Int) {
        mBaseApiManager.clientService.deleteClientImage(clientId)
    }

    /**
     * This Method will be called when ever user want to update the client profile image.
     * The response of the updating the client image can be true or false its depends upon the
     * network and right choice image file.
     *
     * @param id   Client Id
     * @param file MultipartBody of the Image file
     * @return ResponseBody is the Retrofit 2 response
     */
    suspend fun uploadClientImage(clientId: Int, file: MultiPartFormDataContent) {
        return mBaseApiManager.clientService.uploadClientImage(clientId, file)
    }

    fun getClientImage(clientId: Int): Flow<DataState<String>> {
        return mBaseApiManager.clientService.getClientImage(clientId)
            .asDataStateFlow()
            .map {
                    response ->
                when (response) {
                    is DataState.Success -> {
                        val encodedString = response.data.bodyAsText()
                        val pureBase64Encoded = encodedString.substringAfter(',')
                        DataState.Success(pureBase64Encoded)
                    }
                    is DataState.Error -> DataState.Error(response.exception)
                    DataState.Loading -> DataState.Loading
                }
            }
    }

    /**
     * Return Clients from DatabaseHelperClient only one time.
     */

    /**
     * This Method will be called when ever user create the client. if user is in online mode
     * then request goes to the server to get the client template and in response client template
     * saves in table in background and return the Observable.just(clientTemplate) to the presenter
     * and if user in offline mode then we load the client Template from Database.
     *
     * @return ClientTemplate
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val clientTemplate: Flow<ClientsTemplateEntity>
        get() = prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false ->
                    flow {
                        val clientsTemplate = mBaseApiManager.clientService.getClientTemplate()
                        clientDatabaseHelper.saveClientTemplate(clientsTemplate)
                        emit(clientsTemplate)
                    }

                true ->
                    /**
                     * Return Clients from DatabaseHelperClient only one time.
                     */
                    /**
                     * Return Clients from DatabaseHelperClient only one time.
                     */
                    clientDatabaseHelper.readClientTemplate()
            }
        }

    /**
     * This Method create the client by making directly request to server when User is Online
     * and give response client type is created otherwise give error string.
     * if user if offline mode then client details failed by user is saved into Database directly.
     *
     * @param clientPayload Client details filled by user
     * @return Client
     */
    suspend fun createClient(clientPayload: ClientPayloadEntity): Int? {
        return if (prefManager.userInfo.first().userStatus) {
            clientDatabaseHelper.saveClientPayloadToDB(clientPayload)
            null
        } else {
            mBaseApiManager.clientService.createClient(clientPayload)?.clientId
        }
    }

    /**
     * This Method updates the client details
     * @param clientPayload Client details filled by user
     * @return Client
     */
    suspend fun updateClient(clientId: Int, clientPayload: ClientPayloadEntity): Int? {
        return mBaseApiManager.clientService.updateClient(clientId, clientPayload)?.clientId
    }

    /**
     * Loading All Client payload from database to sync to the server
     *
     * @return List<ClientPayload></ClientPayload>>
     */
    val allDatabaseClientPayload: Flow<List<ClientPayloadEntity>>
        get() = clientDatabaseHelper.readAllClientPayload()

    /**
     * This method will called when user is syncing the client created from Database.
     * whenever a client is synced then request goes to Database to delete that client form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the clientPayload in Database
     * @return List<ClientPayload></ClientPayload>>
     */
    fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long,
    ): Flow<List<ClientPayloadEntity>> {
        return clientDatabaseHelper.deleteAndUpdatePayloads(id, clientCreationTIme)
    }

    /**
     * This Method for Updating the Client Payload into the Database.
     *
     * @param clientPayload ClientPayload
     * @return ClientPayload
     */
    suspend fun updateClientPayload(clientPayload: ClientPayloadEntity) {
        clientDatabaseHelper.updateDatabaseClientPayload(clientPayload)
    }

    /**
     * This Method is fetching the Client Pinpoint location from the DataTable
     * "client_pinpoint_location"
     *
     * @param clientId Client Id
     * @return ClientAddressResponse
     */
    fun getClientPinpointLocations(clientId: Int): Flow<List<ClientAddressResponse>> {
        return mBaseApiManager.clientService.getClientPinpointLocations(clientId)
    }

    /**
     * This Method is adding the new address in the DataTable "client_pinpoint_location"
     *
     * @param clientId Client Id
     * @param address  Client Address
     * @return GenericResponse
     */
    suspend fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest?,
    ): PinpointLocationActionResponse {
        return mBaseApiManager.clientService.addClientPinpointLocation(clientId, address)
    }

    /**
     * This Method is deleting the client address from the DataTable "client_pinpoint_location"
     *
     * @param apptableId
     * @param datatableId
     * @return GenericResponse
     */
    suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int,
    ): PinpointLocationActionResponse {
        return mBaseApiManager.clientService.deleteClientPinpointLocation(
            apptableId,
            datatableId,
        )
    }

    /**
     * This Method is updating the client address from the DataTable "client_pinpoint_location"
     *
     * @param apptableId
     * @param datatableId
     * @param address     Client Address
     * @return GenericResponse
     */
    suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest?,
    ): PinpointLocationActionResponse {
        return mBaseApiManager.clientService.updateClientPinpointLocation(
            apptableId,
            datatableId,
            address,
        )
    }

    /**
     * This method is activating the client
     *
     * @param clientId
     * @return GenericResponse
     */
    suspend fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?,
    ): PostClientsClientIdResponse {
        return mBaseApiManager.clientsApi.activate1(
            clientId.toLong(),
            PostClientsClientIdRequest(
                activationDate = clientActivate?.activationDate,
                dateFormat = clientActivate?.dateFormat,
                locale = clientActivate?.locale,
            ),
            "activate",
        )
    }

    /**Add commentMore actions
     * Gets the address configuration.
     *
     * @return The address configuration.
     */
    suspend fun getAddressConfiguration(): AddressConfiguration {
        return mBaseApiManager.clientService.getAddressConfiguration()
    }

    /**
     * Gets the address template.
     *
     * The address template is a predefined format for addresses that can be used to ensure consistency
     * and accuracy when collecting and storing address information.
     *
     * @return The address template.
     */
    suspend fun getAddressTemplate(): AddressTemplate {
        return mBaseApiManager.clientService.getAddressTemplate()
    }

    suspend fun getClientAddresses(clientId: Int): List<ClientAddressEntity> {
        return mBaseApiManager.clientService.getClientAddresses(clientId = clientId)
    }

    suspend fun createClientAddress(
        clientId: Int,
        addressTypeId: Int,
        addressRequest: PostClientAddressRequest,
    ): PostClientAddressResponse {
        return mBaseApiManager.clientService.createClientAddress(
            clientId = clientId,
            addressTypeId = addressTypeId,
            addressPayload = addressRequest,
        )
    }

    suspend fun assignClientStaff(
        clientId: Int,
        staffId: Int,
    ): HttpResponse {
        return mBaseApiManager.clientService.assignStaff(
            clientId = clientId,
            payload = AssignStaffRequest(staffId),
        )
    }

    suspend fun unAssignClientStaff(
        clientId: Int,
        staffId: Int,
    ): HttpResponse {
        return mBaseApiManager.clientService.unassignStaff(
            clientId = clientId,
            payload = AssignStaffRequest(staffId),
        )
    }

    suspend fun proposeClientTransfer(
        clientId: Int,
        destinationOfficeId: Int,
        transferDate: String,
        note: String,
    ): HttpResponse {
        return mBaseApiManager.clientService.proposeTransfer(
            clientId = clientId,
            payload = ProposeTransferRequest(
                destinationOfficeId = destinationOfficeId,
                transferDate = transferDate,
                note = note,
                locale = "en",
                dateFormat = "dd-MM-yyyy",
            ),
        )
    }

    suspend fun closeClient(
        clientId: Int,
        closureDate: String,
        closureReasonId: Int,
    ): HttpResponse {
        return mBaseApiManager.clientService.closeClient(
            clientId = clientId,
            payload = ClientCloseRequest(
                closureDate = closureDate,
                closureReasonId = closureReasonId,
                dateFormat = "dd-MM-yyyy",
                locale = "en",
            ),
        )
    }

    suspend fun updateDefaultSavingsAccount(
        clientId: Int,
        savingsId: Long,
    ): HttpResponse {
        return mBaseApiManager.clientService.updateSavingsAccount(
            clientId = clientId,
            payload = UpdateSavingsAccountRequest(savingsId),
        )
    }

    suspend fun createCollateral(
        clientId: Int,
        collateralId: Int,
        quantity: String,
    ): HttpResponse {
        return mBaseApiManager.clientService.createCollateral(
            clientId = clientId,
            payload = CollateralPayload(
                collateralId = collateralId,
                quantity = quantity,
                locale = "en",
            ),
        )
    }
}
