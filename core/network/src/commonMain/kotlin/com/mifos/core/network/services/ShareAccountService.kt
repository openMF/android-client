/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.network.model.share.ShareAccountPayload
import com.mifos.core.network.model.share.ShareTemplate
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

interface ShareAccountService {
    @GET("accounts/" + APIEndPoint.SHARE + "/template")
    fun shareProductTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int?,
    ): Flow<ShareTemplate>

    @POST("accounts/" + APIEndPoint.SHARE)
    suspend fun createShareAccount(
        @Body shareAccountPayload: ShareAccountPayload,
    ): HttpResponse
}
