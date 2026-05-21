/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.group.api

import com.mifos.core.model.objects.clients.ActivatePayload
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

/**
 * Fineract `groups` endpoints. Future group-feature waves add more methods here.
 */
interface GroupApi {

    @POST("groups/{groupId}?command=activate")
    suspend fun activate(
        @Path("groupId") groupId: Int,
        @Body payload: ActivatePayload,
    ): HttpResponse
}
