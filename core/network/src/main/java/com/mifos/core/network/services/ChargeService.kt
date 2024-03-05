/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.data.ChargesPayload
import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.templates.clients.ChargeTemplate
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * @author nellyk
 */
interface ChargeService {
    @GET(APIEndPoint.CHARGES)
    fun listAllCharges(): Observable<ResponseBody>

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges/template")
    fun getAllChargesS(@Path("clientId") clientId: Int): Observable<ChargeTemplate>

    @GET(APIEndPoint.LOANS + "/{loanId}/charges/template")
    fun getAllChargeV3(@Path("loanId") loanId: Int): Observable<ResponseBody>

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges")
    fun getListOfCharges(
        @Path("clientId") clientId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Observable<Page<Charges>>

    @POST(APIEndPoint.CLIENTS + "/{clientId}/charges")
    fun createCharges(
        @Path("clientId") clientId: Int,
        @Body chargesPayload: ChargesPayload?
    ): Observable<ChargeCreationResponse>

    @GET(APIEndPoint.LOANS + "/{loanId}/charges")
    fun getListOfLoanCharges(@Path("loanId") loanId: Int): Observable<Page<Charges>>

    @POST(APIEndPoint.LOANS + "/{loanId}/charges")
    fun createLoanCharges(
        @Path("loanId") loanId: Int,
        @Body chargesPayload: ChargesPayload?
    ): Observable<ChargeCreationResponse>
}