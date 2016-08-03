/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.templates.clients.ClientsTemplate;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface ClientService {

    /**
     * @param b      True Enabling the Pagination of the API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum size of the Client
     * @return List of Clients
     */
    @GET(APIEndPoint.CLIENTS)
    Observable<Page<Client>> getAllClients(@Query("paged") boolean b,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit);

    //TODO Remove this method and above method for fetching Client list
    @GET(APIEndPoint.CLIENTS)
    Observable<Page<Client>> getAllClients();

    //TODO Remove this method and above method for fetching Client list
    @GET(APIEndPoint.CLIENTS)
    Observable<Page<Client>> getAllClients(@Query("offset") int offset,
                                           @Query("limit") int limit);

    @GET(APIEndPoint.CLIENTS + "/{clientId}")
    Observable<Client> getClient(@Path("clientId") int clientId);

    @Multipart
    @POST(APIEndPoint.CLIENTS + "/{clientId}/images")
    Observable<ResponseBody> uploadClientImage(@Path("clientId") int clientId,
                                               @Part MultipartBody.Part file);

    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/images")
    Observable<ResponseBody> deleteClientImage(@Path("clientId") int clientId);

    //TODO: Implement when API Fixed
//    @GET("/clients/{clientId}/images")
//    Observable<TypedString> getClientImage(@Path("clientId") int clientId);

    @POST(APIEndPoint.CLIENTS)
    Observable<Client> createClient(@Body ClientPayload clientPayload);

    @GET(APIEndPoint.CLIENTS + "/template")
    Observable<ClientsTemplate> getClientTemplate();

    @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getClientAccounts(@Path("clientId") int clientId);
}
