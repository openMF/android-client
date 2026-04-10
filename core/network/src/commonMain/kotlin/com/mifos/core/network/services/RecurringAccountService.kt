/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

interface RecurringAccountService {

    @POST(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS)
    fun createRecurringDepositAccount(
        @Body recurringDepositAccountPayload: RecurringDepositAccountPayload?,
    ): Flow<HttpResponse>

    @GET(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS + "/template")
    fun getRecurringDepositAccountTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int?,
    ): Flow<RecurringDepositAccountTemplate>
}
