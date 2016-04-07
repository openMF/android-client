package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.ClientAddressAddRequest;
import com.mifos.api.model.ClientAddress;
import com.mifos.api.model.ClientAddressResponse;
import com.mifos.api.model.ClientAddressUpdateRequest;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;


/**
 * Created by Tarun on 04/04/2016.
 */
public interface ClientAddressListService {
    @GET(APIEndPoint.DATATABLES + "/clientAddress/{clientId}")
    void receiveClientAddressList(@Path("clientId") int clientId, Callback<List<ClientAddress>> callback);

    @PUT(APIEndPoint.DATATABLES + "/clientAddress/{clientId}/{rowId}")
    void updateAddressEntry (@Path("clientId") int clientId, @Path("rowId") int rowId,
                             @Body ClientAddressUpdateRequest request,
                             Callback<ClientAddressResponse> callback);

    @POST(APIEndPoint.DATATABLES + "/clientAddress/{clientId}")
    void addAddressEntry (@Path("clientId") int clieentId,
                          @Body ClientAddressAddRequest request,
                          Callback<ClientAddressResponse>  callback);
}
