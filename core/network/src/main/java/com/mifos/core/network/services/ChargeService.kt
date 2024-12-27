/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.payloads.ChargesPayload
import com.mifos.core.model.APIEndPoint
import com.mifos.core.modelobjects.clients.ChargeCreationResponse
import com.mifos.core.objects.client.Charges
import com.mifos.core.modelobjects.clients.Page
import com.mifos.core.modelobjects.template.client.ChargeTemplate
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
    suspend fun getAllChargesS(@Path("clientId") clientId: Int): ChargeTemplate

    @GET(APIEndPoint.LOANS + "/{loanId}/charges/template")
    suspend fun getAllChargeV3(@Path("loanId") loanId: Int): ResponseBody

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges")
    fun getListOfCharges(
        @Path("clientId") clientId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Observable<Page<Charges>>

    @POST(APIEndPoint.CLIENTS + "/{clientId}/charges")
    suspend fun createCharges(
        @Path("clientId") clientId: Int,
        @Body chargesPayload: ChargesPayload,
    ): ChargeCreationResponse

    @GET(APIEndPoint.LOANS + "/{loanId}/charges")
    fun getListOfLoanCharges(@Path("loanId") loanId: Int): Observable<Page<Charges>>

    @POST(APIEndPoint.LOANS + "/{loanId}/charges")
    suspend fun createLoanCharges(
        @Path("loanId") loanId: Int,
        @Body chargesPayload: ChargesPayload,
    ): ChargeCreationResponse
}
