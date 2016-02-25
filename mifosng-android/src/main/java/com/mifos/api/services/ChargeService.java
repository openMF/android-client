/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.services.data.ChargesPayload;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @author
 */
public interface ChargeService {
    @GET(APIEndPoint.CHARGES)
    void listAllCharges(Callback<Response> chargesCallback);

    @GET(APIEndPoint.CHARGES)
    void getAllChargesS(Callback<List<Charges>> callback);

    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
    void getListOfCharges(@Path("clientId") int clientId, Callback<Page<Charges>> chargeListCallback);

    @POST(APIEndPoint.CLIENTS + "/{clientId}/charges")
    void createCharges(@Path("clientId") int clientId, @Body ChargesPayload chargesPayload, Callback<Charges> callback);
}
