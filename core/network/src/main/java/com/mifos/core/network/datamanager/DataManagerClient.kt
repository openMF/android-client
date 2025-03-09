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

import android.util.Log
import com.mifos.core.common.utils.Resource
import com.mifos.core.common.utils.asResourceFlow
import com.mifos.core.databasehelper.DatabaseHelperClient
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.mappers.clients.GetClientResponseMapper
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.ActivatePayload
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.noncore.Identifier
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import com.mifos.core.objects.templates.clients.ClientsTemplate
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.openapitools.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import org.openapitools.client.models.PostClientsClientIdRequest
import org.openapitools.client.models.PostClientsClientIdResponse
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Client API, In which Request is going to Server
 * and In Response, We are getting Client API Observable Response using Retrofit2 .
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
class DataManagerClient @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val mDatabaseHelperClient: DatabaseHelperClient,
    private val prefManager: PrefManager,
) {
    suspend fun getAllClients(offset: Int, limit: Int): Page<Client> {
        return try {
            mBaseApiManager.clientsApi.getAllClients(true, offset, limit).let(
                GetClientResponseMapper::mapFromEntity,
            )
        } catch (e: Exception) {
            Log.d("Client", e.message.toString())
            throw e
        }
    }

    /**
     * This Method Request to the DatabaseHelperClient and DatabaseHelperClient Read the All
     * clients from Client_Table and give the response Page of List of Client
     *
     * @return Page of Client List
     */
    val allDatabaseClients: Observable<Page<Client>>
        get() = mDatabaseHelperClient.readAllClients()

    suspend fun getClient(clientId: Int): Client {
        return mBaseApiManager.clientsApi.getClient(clientId)
    }

    fun syncClientInDatabase(client: Client): Observable<Client> {
        return mDatabaseHelperClient.saveClient(client)
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
        return mBaseApiManager.clientsApi.getClientAccounts(clientId)
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
        return mBaseApiManager.clientsApi.getClientAccounts(clientId)
    }

    /**
     * This Method for removing the Client Image from his profile on server
     * if its response is true the client does not have any profile Image and if
     * response is false then failed to update the client image from server profile.
     * There can any problem during updating the client image like Network error.
     *
     * @param clientId Client ID
     * @return ResponseBody is the Retrofit 2 response
     */
    fun deleteClientImage(clientId: Int): Observable<ResponseBody> {
        return mBaseApiManager.clientsApi.deleteClientImage(clientId)
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
    fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody> {
        return mBaseApiManager.clientsApi.uploadClientImage(id, file)
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
    val clientTemplate: Observable<ClientsTemplate>
        get() = when (prefManager.userStatus) {
            false ->
                mBaseApiManager.clientsApi.clientTemplate
                    .concatMap { clientsTemplate ->
                        mDatabaseHelperClient.saveClientTemplate(
                            clientsTemplate,
                        )
                    }

            true ->
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                mDatabaseHelperClient.readClientTemplate()
        }

    /**
     * This Method create the client by making directly request to server when User is Online
     * and give response client type is created otherwise give error string.
     * if user if offline mode then client details failed by user is saved into Database directly.
     *
     * @param clientPayload Client details filled by user
     * @return Client
     */
    fun createClient(clientPayload: ClientPayload): Observable<Client> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.clientsApi.createClient(clientPayload)
                .concatMap { client -> Observable.just(client) }

            true ->
                /**
                 * If user is in offline mode and he is making client. client payload will be saved
                 * in Database for future synchronization to sever.
                 */
                mDatabaseHelperClient.saveClientPayloadToDB(clientPayload)
        }
    }

    /**
     * Loading All Client payload from database to sync to the server
     *
     * @return List<ClientPayload></ClientPayload>>
     */
    val allDatabaseClientPayload: Observable<List<ClientPayload>>
        get() = mDatabaseHelperClient.readAllClientPayload()

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
    ): Observable<List<ClientPayload>> {
        return mDatabaseHelperClient.deleteAndUpdatePayloads(id, clientCreationTIme)
    }

    /**
     * This Method for Updating the Client Payload into the Database.
     *
     * @param clientPayload ClientPayload
     * @return ClientPayload
     */
    fun updateClientPayload(clientPayload: ClientPayload): Observable<ClientPayload> {
        return mDatabaseHelperClient.updateDatabaseClientPayload(clientPayload)
    }

    /**
     * This Method is for fetching the Client identifier from the REST API.
     *
     * @param clientId Client Id
     * @return List<Identifier>
     </Identifier> */
    suspend fun getClientIdentifiers(clientId: Int): List<Identifier> {
        return mBaseApiManager.clientsApi.getClientIdentifiers(clientId)
    }

    /**
     * This Method is, for creating the Client Identifier.
     *
     * @param clientId          Client Id
     * @param identifierPayload IdentifierPayload
     * @return IdentifierCreationResponse
     */
    fun createClientIdentifier(
        clientId: Int,
        identifierPayload: IdentifierPayload,
    ): Flow<Resource<IdentifierCreationResponse>> {
        return mBaseApiManager.clientsApi.createClientIdentifier(clientId, identifierPayload)
            .asResourceFlow()
    }

    /**
     * This Method is, for fetching the Client Identifier Template.
     *
     * @param clientId Client Id
     * @return IdentifierTemplate
     */
    fun getClientIdentifierTemplate(clientId: Int): Flow<Resource<IdentifierTemplate>> {
        return mBaseApiManager.clientsApi.getClientIdentifierTemplate(clientId).asResourceFlow()
    }

    /**
     * This Method is for deleting the Client Identifier.
     *
     * @param clientId     Client Id
     * @param identifierId Identifier Id
     * @return GenericResponse
     */
    suspend fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int,
    ): DeleteClientsClientIdIdentifiersIdentifierIdResponse {
        return mBaseApiManager.clientsApi.deleteClientIdentifier(clientId, identifierId)
    }

    /**
     * This Method is fetching the Client Pinpoint location from the DataTable
     * "client_pinpoint_location"
     *
     * @param clientId Client Id
     * @return ClientAddressResponse
     */
    suspend fun getClientPinpointLocations(clientId: Int): List<ClientAddressResponse> {
        return mBaseApiManager.clientsApi.getClientPinpointLocations(clientId)
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
    ): GenericResponse {
        return mBaseApiManager.clientsApi.addClientPinpointLocation(clientId, address)
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
    ): GenericResponse {
        return mBaseApiManager.clientsApi
            .deleteClientPinpointLocation(apptableId, datatableId)
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
    ): GenericResponse {
        return mBaseApiManager.clientsApi.updateClientPinpointLocation(
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
        return mBaseApiManager.clientsApi.activateClient(
            clientId.toLong(),
            PostClientsClientIdRequest(
                activationDate = clientActivate?.activationDate,
                dateFormat = clientActivate?.dateFormat,
                locale = clientActivate?.locale,
            ),
            "activate",
        )
    }
}
