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
import rx.Observable;

/**
 * @author
 */
public interface ChargeService {
    @GET(APIEndPoint.CHARGES)
    void listAllCharges(Callback<Response> chargesCallback);

    @GET(APIEndPoint.CHARGES)
    Observable<List<Charges>> getAllChargesS();

    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
    Observable<Page<Charges>> getListOfCharges(@Path("clientId") int clientId);

    @POST(APIEndPoint.CLIENTS + "/{clientId}/charges")
    Observable<Charges> createCharges(@Path("clientId") int clientId, @Body ChargesPayload chargesPayload);
}
