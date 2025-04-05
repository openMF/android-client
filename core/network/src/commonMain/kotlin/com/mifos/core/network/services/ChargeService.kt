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

import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.client.ChargesEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * @author nellyk
 */
interface ChargeService {
    @GET(APIEndPoint.CHARGES)
    fun listAllCharges(): Flow<HttpResponse>

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges/template")
    suspend fun getAllChargesS(@Path("clientId") clientId: Int): ChargeTemplate

    @GET(APIEndPoint.LOANS + "/{loanId}/charges/template")
    suspend fun getAllChargeV3(@Path("loanId") loanId: Int): HttpResponse

    @GET(APIEndPoint.CLIENTS + "/{clientId}/charges")
    fun getListOfCharges(
        @Path("clientId") clientId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Flow<Page<ChargesEntity>>

    @POST(APIEndPoint.CLIENTS + "/{clientId}/charges")
    suspend fun createCharges(
        @Path("clientId") clientId: Int,
        @Body chargesPayload: ChargesPayload,
    ): ChargeCreationResponse

    @GET(APIEndPoint.LOANS + "/{loanId}/charges")
    fun getListOfLoanCharges(@Path("loanId") loanId: Int): Flow<Page<ChargesEntity>>

    @POST(APIEndPoint.LOANS + "/{loanId}/charges")
    suspend fun createLoanCharges(
        @Path("loanId") loanId: Int,
        @Body chargesPayload: ChargesPayload,
    ): ChargeCreationResponse
}
