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
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author nellyk
 */
public interface ChargeService {
    @GET(APIEndPoint.CHARGES)
    void listAllCharges(Callback<Response> chargesCallback);

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges/template")
    void getAllChargesS(@Path("clientId") int clientId, Callback<Response> callback);

    @GET(APIEndPoint.LOANS + "/{loanId}/charges/template")
    void getAllChargev3(@Path ("loanId") int loanId,Callback<Response> callback);

    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
    void getListOfCharges(@Path("clientId") int clientId, Callback<Page<Charges>> chargeListCallback);

    @POST(APIEndPoint.CLIENTS + "/{clientId}"+ APIEndPoint.CHARGES)
    void createCharges(@Path("clientId") int clientId, @Body ChargesPayload chargesPayload, Callback<Charges> callback);

    @GET(APIEndPoint.LOANS + "/{loanId}" + APIEndPoint.CHARGES)
    void getListOfLoanCharges(@Path("loanId") int loanId,Callback<Page<Charges>> loanchargeListCallback);


    @POST(APIEndPoint.LOANS +"/{loanId}/charges")
    void createLoanCharges(@Path("loanId") int loanId,@Body ChargesPayload chargesPayload, Callback<Charges> callback);

    @POST("/"+APIEndPoint.SAVINGSACCOUNTS + "/{accountId}"+ APIEndPoint.CHARGES)
    void createSavingsCharges(@Path("accountId") int accountId, @Body ChargesPayload chargesPayload, Callback<Charges> callback);

    @GET("/"+APIEndPoint.SAVINGSACCOUNTS + "/{accountId}" + APIEndPoint.CHARGES)
    void getSavingsCharges(@Path("accountId") int clientId, Callback<Page<Charges>> callback);

    @GET("/"+APIEndPoint.SAVINGSACCOUNTS + "/{accountId}/charges/template")
    void getAllSavingsCharges(@Path("accountId") int accountId, Callback<Response> callback);
}
