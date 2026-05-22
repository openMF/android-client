/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.client.api

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
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.model.GenericResponse
import com.mifos.core.model.network.ClientCloseTemplateResponse
import com.mifos.core.model.network.CollateralItem
import com.mifos.core.model.network.CollateralItemResult
import com.mifos.core.model.network.GetClientsPageItemsResponse
import com.mifos.core.model.network.PinpointLocationActionResponse
import com.mifos.core.model.network.PostClientAddressRequest
import com.mifos.core.model.network.PostClientAddressResponse
import com.mifos.core.model.network.PostClientsClientIdResponse
import com.mifos.core.network.APIEndPoint
import com.mifos.room.client.entity.ClientAccounts
import com.mifos.room.client.entity.AddressConfiguration
import com.mifos.room.client.entity.AddressTemplate
import com.mifos.room.client.entity.ClientEntity
import com.mifos.room.client.entity.ClientPayloadEntity
import com.mifos.room.client.entity.ClientsTemplateEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse

/** Fineract `client` domain endpoints. */
interface ClientApi {

    // ─────────────────────────────────────────────────────────────────────
    // region — Client base CRUD
    // ─────────────────────────────────────────────────────────────────────

    /** Retrieve a paginated list of clients. */
    @GET(APIEndPoint.CLIENTS)
    suspend fun getAllClients(
        @de.jensklingenberg.ktorfit.http.Query("paged") paged: Boolean,
        @de.jensklingenberg.ktorfit.http.Query("offset") offset: Int,
        @de.jensklingenberg.ktorfit.http.Query("limit") limit: Int,
    ): Page<ClientEntity>

    /** Retrieve a single client by id. */
    @GET(APIEndPoint.CLIENTS + "/{clientId}")
    suspend fun getClient(@Path("clientId") clientId: Int): ClientEntity

    /** Create a new client. */
    @POST(APIEndPoint.CLIENTS)
    suspend fun createClient(@Body clientPayload: ClientPayloadEntity?): ClientEntity?

    /** Update an existing client. */
    @PUT(APIEndPoint.CLIENTS + "/{clientId}")
    suspend fun updateClient(
        @Path("clientId") clientId: Int,
        @Body clientPayload: ClientPayloadEntity?,
    ): ClientEntity?

    /** Retrieve the client creation template (dropdown options, validations). */
    @GET(APIEndPoint.CLIENTS + "/template")
    suspend fun getClientTemplate(): ClientsTemplateEntity

    /** Retrieve the client edit template (per-client dropdown options). */
    @GET("clients/{clientId}?template=true&staffInSelectedOfficeOnly=true")
    suspend fun getClientEditTemplate(
        @Path("clientId") clientId: Int,
    ): GetClientsPageItemsResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Client lifecycle (activate, close, transfer, assign)
    // ─────────────────────────────────────────────────────────────────────

    /** Activate a pending client. */
    @POST(APIEndPoint.CLIENTS + "/{clientId}?command=activate")
    suspend fun activate(
        @Path("clientId") clientId: Int,
        @Body payload: ActivatePayload,
    ): PostClientsClientIdResponse

    /** Close an active client. */
    @POST("clients/{clientId}?command=close")
    suspend fun closeClient(
        @Path("clientId") clientId: Int,
        @Body payload: ClientCloseRequest,
    ): HttpResponse

    /** Retrieve the close-client template (reason codes, dates). */
    @GET("clients/template?commandParam=close")
    suspend fun getClientCloseTemplate(): ClientCloseTemplateResponse

    /** Assign a staff member to a client. */
    @POST("clients/{clientId}?command=assignStaff")
    suspend fun assignStaff(
        @Path("clientId") clientId: Int,
        @Body payload: AssignStaffRequest,
    ): HttpResponse

    /** Unassign the current staff member from a client. */
    @POST("clients/{clientId}?command=unassignStaff")
    suspend fun unassignStaff(
        @Path("clientId") clientId: Int,
        @Body payload: AssignStaffRequest,
    ): HttpResponse

    /** Propose a client transfer between offices. */
    @POST("clients/{clientId}?command=proposeTransfer")
    suspend fun proposeTransfer(
        @Path("clientId") clientId: Int,
        @Body payload: ProposeTransferRequest,
    ): HttpResponse

    /** Update the linked savings account on a client. */
    @POST("clients/{clientId}?command=updateSavingsAccount")
    suspend fun updateSavingsAccount(
        @Path("clientId") clientId: Int,
        @Body payload: UpdateSavingsAccountRequest,
    ): HttpResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Client images
    // ─────────────────────────────────────────────────────────────────────

    /** Upload a client profile image (multipart). */
    @POST(APIEndPoint.CLIENTS + "/{clientId}/images")
    suspend fun uploadClientImage(
        @Path("clientId") clientId: Int,
        @Body body: MultiPartFormDataContent,
    )

    /** Delete the client profile image. */
    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/images")
    suspend fun deleteClientImage(@Path("clientId") clientId: Int)

    /** Retrieve the client profile image bytes. */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/images")
    suspend fun getClientImage(@Path("clientId") clientId: Int): HttpResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Client accounts (loans, savings, share, etc.)
    // ─────────────────────────────────────────────────────────────────────

    /** Retrieve all accounts (loan + savings + share) belonging to a client. */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    suspend fun getClientAccounts(@Path("clientId") clientId: Int): ClientAccounts

    // ─────────────────────────────────────────────────────────────────────
    // region — Client pinpoint locations
    // ─────────────────────────────────────────────────────────────────────

    /** List pinpoint locations for a client. */
    @GET(APIEndPoint.DATATABLES + "/client_pinpoint_location/{clientId}")
    suspend fun getClientPinpointLocations(
        @Path("clientId") clientId: Int,
    ): List<ClientAddressResponse>

    /** Add a pinpoint location for a client. */
    @POST(APIEndPoint.DATATABLES + "/client_pinpoint_location/{clientId}")
    suspend fun addClientPinpointLocation(
        @Path("clientId") clientId: Int,
        @Body clientAddressRequest: ClientAddressRequest?,
    ): PinpointLocationActionResponse

    /** Delete a specific pinpoint location row. */
    @DELETE(APIEndPoint.DATATABLES + "/client_pinpoint_location/{apptableId}/{datatableId}")
    suspend fun deleteClientPinpointLocation(
        @Path("apptableId") apptableId: Int,
        @Path("datatableId") datatableId: Int,
    ): PinpointLocationActionResponse

    /** Update a specific pinpoint location row. */
    @PUT(APIEndPoint.DATATABLES + "/client_pinpoint_location/{apptableId}/{datatableId}")
    suspend fun updateClientPinpointLocation(
        @Path("apptableId") apptableId: Int,
        @Path("datatableId") datatableId: Int,
        @Body address: ClientAddressRequest?,
    ): PinpointLocationActionResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Client addresses (structured Fineract address API)
    // ─────────────────────────────────────────────────────────────────────

    /** Retrieve global address configuration (which fields are enabled). */
    @GET("configurations/name/enable-address")
    suspend fun getAddressConfiguration(): AddressConfiguration

    /** Retrieve the global address template (country/state/city codes). */
    @GET("client/addresses/template")
    suspend fun getAddressTemplate(): AddressTemplate

    /** List structured addresses belonging to a client. */
    @GET("client/{clientId}/addresses")
    suspend fun getClientAddresses(
        @Path("clientId") clientId: Int,
    ): List<ClientAddressEntity>

    /** Add a structured address to a client. */
    @POST("client/{clientId}/addresses?type={addressTypeId}")
    suspend fun createClientAddress(
        @Path("clientId") clientId: Int,
        @Path("addressTypeId") addressTypeId: Int,
        @Body addressPayload: PostClientAddressRequest,
    ): PostClientAddressResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Client collateral
    // ─────────────────────────────────────────────────────────────────────

    /** List globally-available collateral item options. */
    @GET("collateral-management")
    suspend fun getCollateralItems(): List<CollateralItem>

    /** List collateral options attached to a client (template). */
    @GET("clients/{clientId}/collaterals/template")
    suspend fun getClientCollateralItems(
        @Path("clientId") clientId: Int,
    ): List<CollateralItemResult>

    /** Create a new collateral entry for a client. */
    @POST("clients/{clientId}/collaterals")
    suspend fun createCollateral(
        @Path("clientId") clientId: Int,
        @Body payload: CollateralPayload,
    ): HttpResponse

    // ─────────────────────────────────────────────────────────────────────
    // region — Client identifiers
    // ─────────────────────────────────────────────────────────────────────

    /** List identifiers belonging to a client (passport / national id / etc.). */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS)
    suspend fun getClientIdentifiers(@Path("clientId") clientId: Long): List<Identifier>

    /** Retrieve a single client identifier. */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    suspend fun getClientIdentifier(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
    ): Identifier

    /** Retrieve the client-identifier template (allowed document types). */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/identifiers/template")
    suspend fun getClientIdentifierTemplate(
        @Path("clientId") clientId: Long,
    ): IdentifierTemplate

    /** Add a new identifier to a client. */
    @POST(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS)
    suspend fun createClientIdentifier(
        @Path("clientId") clientId: Long,
        @Body identifierPayload: IdentifierPayload,
    ): GenericResponse

    /** Update an existing client identifier. */
    @PUT(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    suspend fun updateClientIdentifier(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
        @Body identifierPayload: IdentifierPayload,
    ): GenericResponse

    /** Delete a client identifier. */
    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    suspend fun deleteClientIdentifier(
        @Path("clientId") clientId: Long,
        @Path("identifierId") identifierId: Long,
    ): GenericResponse
}
