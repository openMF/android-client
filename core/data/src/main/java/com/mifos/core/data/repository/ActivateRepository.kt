/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.`object`.clients.ActivatePayload
import com.mifos.core.network.GenericResponse
import org.openapitools.client.models.PostCentersCenterIdResponse
import org.openapitools.client.models.PostClientsClientIdResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface ActivateRepository {

    suspend fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?,
    ): PostClientsClientIdResponse

    suspend fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?,
    ): PostCentersCenterIdResponse

    fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?,
    ): Observable<GenericResponse>
}
