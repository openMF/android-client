/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataManagerRecurringAccount(
    val mBaseApiManager: BaseApiManager,
) {
    fun createRecurringDepositAccount(
        recurringDepositAccountPayload: RecurringDepositAccountPayload?,
    ): Flow<GenericResponse> {
        return mBaseApiManager.recurringSavingsAccountService.createRecurringDepositAccount(
            recurringDepositAccountPayload,
        ).map { response ->

            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)

                throw IllegalStateException(errorMessage)
            }

            val json = Json { ignoreUnknownKeys = true }

            json.decodeFromString<GenericResponse>(response.bodyAsText())
        }
    }

    fun getRecurringDepositAccountTemplate(
        clientId: Int,
        productId: Int?,
    ): Flow<RecurringDepositAccountTemplate> {
        return mBaseApiManager.recurringSavingsAccountService.getRecurringDepositAccountTemplate(
            clientId,
            productId,
        )
    }
}
