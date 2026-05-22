/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.center.api

import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.model.network.PostCentersCenterIdResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

/**
 * Fineract `centers` endpoints. Future center-feature waves add more methods here.
 */
interface CenterApi {

    @POST("centers/{centerId}?command=activate")
    suspend fun activate(
        @Path("centerId") centerId: Int,
        @Body payload: ActivatePayload,
    ): PostCentersCenterIdResponse
}
