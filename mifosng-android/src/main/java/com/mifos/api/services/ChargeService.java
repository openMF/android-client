/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.services.data.ChargesPayload;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author nellyk
 */
public interface ChargeService {
    @GET(APIEndPoint.CHARGES)
    Observable<Response> listAllCharges();

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges/template")
    Observable<Response> getAllChargesS(@Path("clientId") int clientId);

    @GET(APIEndPoint.LOANS + "/{loanId}/charges/template")
    Observable<Response> getAllChargev3(@Path("loanId") int loanId);

    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
    Observable<Page<Charges>> getListOfCharges(@Path("clientId") int clientId);

    @POST(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
    Observable<Charges> createCharges(@Path("clientId") int clientId,
                                      @Body ChargesPayload chargesPayload);

    @GET(APIEndPoint.LOANS + "/{loanId}" + APIEndPoint.CHARGES)
    Observable<Page<Charges>> getListOfLoanCharges(@Path("loanId") int loanId);


    @POST(APIEndPoint.LOANS + "/{loanId}/charges")
    Observable<Charges> createLoanCharges(@Path("loanId") int loanId,
                                          @Body ChargesPayload chargesPayload);

}
