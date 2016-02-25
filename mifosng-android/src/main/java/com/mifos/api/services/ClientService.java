/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.ClientPayload;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * @author fomenkoo
 */
public interface ClientService {

    //This is a default call and Loads client from 0 to 200
    @GET(APIEndPoint.CLIENTS)
    void listAllClients(Callback<Page<Client>> callback);

    @GET(APIEndPoint.CLIENTS)
    void listAllClients(@Query("offset") int offset, @Query("limit") int limit, Callback<Page<Client>> callback);

    @GET(APIEndPoint.CLIENTS + "/{clientId}")
    void getClient(@Path("clientId") int clientId, Callback<Client> clientCallback);

    @Multipart
    @POST(APIEndPoint.CLIENTS + "/{clientId}/images")
    void uploadClientImage(@Path("clientId") int clientId,
                           @Part("file") TypedFile file,
                           Callback<Response> responseCallback);

    @DELETE(APIEndPoint.CLIENTS + "/{clientId}/images")
    void deleteClientImage(@Path("clientId") int clientId, Callback<Response> responseCallback);

    //TODO: Implement when API Fixed
    @GET("/clients/{clientId}/images")
    void getClientImage(@Path("clientId") int clientId, Callback<TypedString> callback);

    @POST(APIEndPoint.CLIENTS)
    void createClient(@Body ClientPayload clientPayload, Callback<Client> callback);

    @GET(APIEndPoint.CLIENTS + "/template")
    void getClientTemplate(Callback<Response> callback);
}
