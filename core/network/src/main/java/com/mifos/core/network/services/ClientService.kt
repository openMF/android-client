/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
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
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface ClientService {
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
        @Query("limit") limit: Int
    ): Observable<Page<Client>>

    @GET(APIEndPoint.CLIENTS + "/{clientId}")
    fun getClient(@Path("clientId") clientId: Int): Observable<Client>

    @Multipart
    @POST(APIEndPoint.CLIENTS + "/{clientId}/images")
    fun uploadClientImage(
        @Path("clientId") clientId: Int,
        @Part file: MultipartBody.Part?
    ): Observable<ResponseBody>

    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/images")
    fun deleteClientImage(@Path("clientId") clientId: Int): Observable<ResponseBody>

    //TODO: Implement when API Fixed
    //    @GET("/clients/{clientId}/images")
    //    Observable<TypedString> getClientImage(@Path("clientId") int clientId);
    @POST(APIEndPoint.CLIENTS)
    fun createClient(@Body clientPayload: ClientPayload?): Observable<Client>

    @get:GET(APIEndPoint.CLIENTS + "/template")
    val clientTemplate: Observable<ClientsTemplate>

    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    fun getClientAccounts(@Path("clientId") clientId: Int): Observable<ClientAccounts>

    /**
     * This Service is for fetching the List of Identifiers.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers
     *
     * @param clientId Client Id
     * @return List<Identifier>
    </Identifier> */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.IDENTIFIERS)
    fun getClientIdentifiers(@Path("clientId") clientId: Int): Observable<List<Identifier>>

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
    fun createClientIdentifier(
        @Path("clientId") clientId: Int,
        @Body identifierPayload: IdentifierPayload?
    ): Observable<IdentifierCreationResponse>

    /**
     * This Service is for the Fetching the Client Identifier Template.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/identifiers/template
     *
     * @param clientId Client Id
     * @return IdentifierTemplate
     */
    @GET(APIEndPoint.CLIENTS + "/{clientId}/identifiers/template")
    fun getClientIdentifierTemplate(@Path("clientId") clientId: Int): Observable<IdentifierTemplate>

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
        @Path("identifierId") identifierId: Int
    ): Observable<GenericResponse>

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
        @Path("clientId") clientId: Int
    ): Observable<List<ClientAddressResponse>>

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
    fun addClientPinpointLocation(
        @Path("clientId") clientId: Int,
        @Body clientAddressRequest: ClientAddressRequest?
    ): Observable<GenericResponse>

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
    fun deleteClientPinpointLocation(
        @Path("apptableId") apptableId: Int,
        @Path("datatableId") datatableId: Int
    ): Observable<GenericResponse>

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
    fun updateClientPinpointLocation(
        @Path("apptableId") apptableId: Int,
        @Path("datatableId") datatableId: Int,
        @Body address: ClientAddressRequest?
    ): Observable<GenericResponse>

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
        @Body clientActivate: ActivatePayload?
    ): Observable<GenericResponse>
}