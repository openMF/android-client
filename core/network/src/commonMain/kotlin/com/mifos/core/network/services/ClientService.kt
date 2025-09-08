/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.model.objects.clients.AssignStaffRequest
import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import com.mifos.core.model.objects.clients.ClientCloseRequest
import com.mifos.core.model.objects.clients.CollateralPayload
import com.mifos.core.model.objects.clients.ProposeTransferRequest
import com.mifos.core.model.objects.clients.UpdateSavingsAccountRequest
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.model.objects.noncoreobjects.IdentifierCreationResponse
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.ClientCloseTemplateResponse
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.network.model.GetClientsPageItemsResponse
import com.mifos.core.network.model.PinpointLocationActionResponse
import com.mifos.core.network.model.PostAuthenticationRequest
import com.mifos.core.network.model.PostAuthenticationResponse
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.network.model.PostClientAddressResponse
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.client.AddressConfiguration
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * @author fomenkoo
 */
interface ClientService {

    @POST("authentication")
    suspend fun authenticate(
        @Body postAuthenticationRequest: PostAuthenticationRequest,
        @Query("returnClientList") returnClientList: Boolean? = false,
    ): PostAuthenticationResponse

    /**
     * @param b      True Enabling the Pagination of the API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum size of the Client
     * @return List of Clients
     */
    @GET(APIEndPoint.CLIENTS)
    fun getAllClients(
        @Query("paged") b: Boolean,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Flow<Page<ClientEntity>>

    @GET(APIEndPoint.CLIENTS + "/{clientId}")
    suspend fun getClient(@Path("clientId") clientId: Int): ClientEntity

    @POST(APIEndPoint.CLIENTS + "/{clientId}/images")
    suspend fun uploadClientImage(
        @Path("clientId") clientId: Int,
        @Body body: MultiPartFormDataContent,
    ): Unit

    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/images")
    suspend fun deleteClientImage(@Path("clientId") clientId: Int)

    @GET(APIEndPoint.CLIENTS + "/{clientId}/images")
    fun getClientImage(@Path("clientId") clientId: Int): Flow<HttpResponse>

    @POST(APIEndPoint.CLIENTS)
    suspend fun createClient(@Body clientPayload: ClientPayloadEntity?): ClientEntity?

    @PUT(APIEndPoint.CLIENTS + "/{clientId}")
    suspend fun updateClient(
        @Path("clientId") clientId: Int,
        @Body clientPayload: ClientPayloadEntity?,
    ): ClientEntity?

    @GET(APIEndPoint.CLIENTS + "/template")
    suspend fun getClientTemplate(): ClientsTemplateEntity

    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    fun getClientAccounts(@Path("clientId") clientId: Int): Flow<ClientAccounts>

    /**
     * This Service is for fetching the List of Identifiers.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers
     *
     * @param clientId Client Id
     * @return List<Identifier>
     </Identifier> */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS)
    fun getClientIdentifiers(@Path("clientId") clientId: Int): Flow<List<Identifier>>

    /**
     * This Service is for Creating the Client Identifier.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers
     *
     * @param clientId          Client Id
     * @param identifierPayload IdentifierPayload
     * @return IdentifierCreationResponse
     */
    @POST(APIEndPoint.CLIENTS + "/{clientId}/identifiers")
    suspend fun createClientIdentifier(
        @Path("clientId") clientId: Int,
        @Body identifierPayload: IdentifierPayload,
    ): IdentifierCreationResponse

    /**
     * This Service is for the Fetching the Client Identifier Template.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/template
     *
     * @param clientId Client Id
     * @return IdentifierTemplate
     */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/identifiers/template")
    fun getClientIdentifierTemplate(@Path("clientId") clientId: Int): Flow<IdentifierTemplate>

    /**
     * This Service for Deleting the Client Identifier.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/
     * {identifierId}
     *
     * @param clientId     Client Id
     * @param identifierId Identifier Id
     * @return GenericResponse
     */
    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    fun deleteClientIdentifier(
        @Path("clientId") clientId: Int,
        @Path("identifierId") identifierId: Int,
    ): Flow<GenericResponse>

    /**
     * This is the service for fetching the client pinpoint locations from the dataTable
     * "client_pinpoint_location". This DataTable entries are
     * 1. Place Id
     * 2. Place Address
     * 3. latitude
     * 4. longitude
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/datatables/client_pinpoint_location
     * /{appTableId}
     *
     * @param clientId Client Id
     * @return ClientAddressResponse
     */
    @GET(APIEndPoint.DATATABLES + "/client_pinpoint_location/{clientId}")
    fun getClientPinpointLocations(
        @Path("clientId") clientId: Int,
    ): Flow<List<ClientAddressResponse>>

    /**
     * This is the service for adding the new Client Pinpoint Location in dataTable
     * "client_pinpoint_location".
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/datatables/client_pinpoint_location
     * /{appTableId}
     *
     * @param clientId             Client Id
     * @param clientAddressRequest ClientAddress
     * @return GenericResponse
     */
    @POST(APIEndPoint.DATATABLES + "/client_pinpoint_location/{clientId}")
    suspend fun addClientPinpointLocation(
        @Path("clientId") clientId: Int,
        @Body clientAddressRequest: ClientAddressRequest?,
    ): PinpointLocationActionResponse

    /**
     * This is the service for deleting the pinpoint location from the DataTable
     * "client_pinpoint_location".
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/datatables/client_pinpoint_location
     * /{appTableId}/{datatableId}
     *
     * @param apptableId
     * @param datatableId
     * @return GenericResponse
     */
    @DELETE(APIEndPoint.DATATABLES + "/client_pinpoint_location/{apptableId}/{datatableId}")
    suspend fun deleteClientPinpointLocation(
        @Path("apptableId") apptableId: Int,
        @Path("datatableId") datatableId: Int,
    ): PinpointLocationActionResponse

    /**
     * This is the service for updating the pinpoint location from DataTable
     * "client_pinpoint_location"
     * REST ENT POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/datatables/client_pinpoint_location
     * /{appTableId}/{datatableId}
     *
     * @param apptableId
     * @param datatableId
     * @param address     Client Address
     * @return GenericResponse
     */
    @PUT(APIEndPoint.DATATABLES + "/client_pinpoint_location/{apptableId}/{datatableId}")
    suspend fun updateClientPinpointLocation(
        @Path("apptableId") apptableId: Int,
        @Path("datatableId") datatableId: Int,
        @Body address: ClientAddressRequest?,
    ): PinpointLocationActionResponse

    /**
     * This is the service to activate the client
     * REST ENT POINT
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}?command=activate
     *
     * @param clientId
     * @return GenericResponse
     */
    @POST(APIEndPoint.CLIENTS + "/{clientId}?command=activate")
    fun activateClient(
        @Path("clientId") clientId: Int,
        @Body clientActivate: ActivatePayload?,
    ): Flow<GenericResponse>

    /**Add commentMore actions
     * Retrieves address configuration from Global Configuration.
     *
     * @return The AddressConfiguration object
     */
    @GET("configurations/name/enable-address")
    suspend fun getAddressConfiguration(): AddressConfiguration

    /**
     * Retrieves an address template.
     * This template can be used to pre-fill address forms or guide users in providing address information.
     *
     * @return An [AddressTemplate] object containing the structure for an address.
     */
    @GET("client/addresses/template")
    suspend fun getAddressTemplate(): AddressTemplate

    @GET("client/{clientId}/addresses")
    suspend fun getClientAddresses(@Path("clientId") clientId: Int): List<ClientAddressEntity>

    @POST("client/{clientId}/addresses?type={addressTypeId}")
    suspend fun createClientAddress(
        @Path("clientId") clientId: Int,
        @Path("addressTypeId") addressTypeId: Int,
        @Body addressPayload: PostClientAddressRequest,
    ): PostClientAddressResponse

    @GET("clients/{clientId}?template=true&staffInSelectedOfficeOnly=true")
    suspend fun getClientTemplate(@Path("clientId") clientId: Int): GetClientsPageItemsResponse

    @GET("clients/template?commandParam=close")
    suspend fun getClientCloseTemplate(): ClientCloseTemplateResponse

    @GET("collateral-management")
    suspend fun getCollateralItems(): List<CollateralItem>

    @POST("clients/{clientId}?command=assignStaff")
    suspend fun assignStaff(
        @Path("clientId") clientId: Int,
        @Body payload: AssignStaffRequest,
    ): HttpResponse

    @POST("clients/{clientId}?command=unassignStaff")
    suspend fun unassignStaff(
        @Path("clientId") clientId: Int,
        @Body payload: AssignStaffRequest,
    ): HttpResponse

    @POST("clients/{clientId}?command=proposeTransfer")
    suspend fun proposeTransfer(
        @Path("clientId") clientId: Int,
        @Body payload: ProposeTransferRequest,
    ): HttpResponse

    @POST("clients/{clientId}?command=updateSavingsAccount")
    suspend fun updateSavingsAccount(
        @Path("clientId") clientId: Int,
        @Body payload: UpdateSavingsAccountRequest,
    ): HttpResponse

    @POST("clients/{clientId}?command=close")
    suspend fun closeClient(
        @Path("clientId") clientId: Int,
        @Body payload: ClientCloseRequest,
    ): HttpResponse

    @POST("clients/{clientId}/collaterals")
    suspend fun createCollateral(
        @Path("clientId") clientId: Int,
        @Body payload: CollateralPayload,
    ): HttpResponse
}
