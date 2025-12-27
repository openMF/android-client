/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.share.ShareAccountPayload
import com.mifos.core.network.model.share.ShareTemplate
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

class DataManagerShare(
    private val baseApiManager: BaseApiManager,
) {

    fun getShareTemplate(clientId: Int, productId: Int?): Flow<ShareTemplate> =
        baseApiManager.shareAccountService.shareProductTemplate(clientId, productId)

    suspend fun createShareAccount(shareAccountPayload: ShareAccountPayload): GenericResponse {
        val response = baseApiManager.shareAccountService.createShareAccount(shareAccountPayload)

        return if (!response.status.isSuccess()) {
            val errorMsg = extractErrorMessage(response)

            throw IllegalStateException(errorMsg)
        } else {
            val json = Json { ignoreUnknownKeys = true }

            json.decodeFromString<GenericResponse>(response.bodyAsText())
        }
    }
}
