/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.fixeddeposit.api

import com.mifos.core.model.GenericResponse
import com.mifos.core.model.network.fixedDeposit.FixedDepositPayload
import com.mifos.core.model.network.fixedDeposit.FixedDepositTemplate
import com.mifos.core.network.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `fixed-deposit` domain endpoints. */
interface FixedDepositApi {

    @GET(APIEndPoint.FIXED_DEPOSIT + "/template")
    suspend fun getFixedDepositProductTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int?,
    ): FixedDepositTemplate

    @POST(APIEndPoint.FIXED_DEPOSIT)
    suspend fun createFixedDepositAccount(
        @Body fixedDepositPayload: FixedDepositPayload,
    ): GenericResponse
}
