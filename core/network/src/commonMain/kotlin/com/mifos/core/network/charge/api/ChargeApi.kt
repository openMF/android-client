/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.charge.api

import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.network.APIEndPoint
import com.mifos.room.charge.entity.ChargesEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

/** Fineract `charge` domain endpoints — top-level polymorphic charges across resource types. */
interface ChargeApi {

    @GET(APIEndPoint.CHARGES)
    suspend fun listAllCharges(): HttpResponse

    @GET("{resourceType}/{resourceId}/charges/template")
    suspend fun getChargeTemplate(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
    ): ChargeTemplate

    @GET("{resourceType}/{resourceId}/charges")
    suspend fun getListOfPagingCharges(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Page<ChargesEntity>

    @GET("{resourceType}/{resourceId}/charges?offset=0&limit=0")
    suspend fun getListOfClientCharges(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
    ): Page<ChargesEntity>

    @GET("{resourceType}/{resourceId}/charges")
    suspend fun getListOfOtherAccountCharge(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
    ): List<ChargesEntity>

    @GET("{resourceType}/{resourceId}/charges/{chargeId}")
    suspend fun getCharge(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
        @Path("chargeId") chargeId: Int,
    ): ChargesEntity

    @POST("{resourceType}/{resourceId}/charges")
    suspend fun createCharges(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
        @Body chargesPayload: ChargesPayload,
    ): HttpResponse

    @DELETE("{resourceType}/{resourceId}/charges/{chargeId}")
    suspend fun deleteCharge(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
        @Path("chargeId") chargeId: Int,
    )

    @PUT("{resourceType}/{resourceId}/charges/{chargeId}")
    suspend fun updateCharge(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Int,
        @Path("chargeId") chargeId: Int,
        @Body payload: ChargesPayload,
    )
}
