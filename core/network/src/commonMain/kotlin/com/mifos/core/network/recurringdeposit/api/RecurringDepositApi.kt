/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.recurringdeposit.api

import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.model.GenericResponse
import com.mifos.core.network.APIEndPoint
import com.mifos.core.model.objects.recurringdeposit.RecurringDepositAccountTemplate
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `recurring-deposit` domain endpoints. */
interface RecurringDepositApi {

    @POST(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS)
    suspend fun createRecurringDepositAccount(
        @Body recurringDepositAccountPayload: RecurringDepositAccountPayload?,
    ): GenericResponse

    @GET(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS + "/template")
    suspend fun getRecurringDepositAccountTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int?,
    ): RecurringDepositAccountTemplate
}
