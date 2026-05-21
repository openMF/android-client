/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.client.api

import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.network.model.PostClientsClientIdResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

/**
 * Fineract `clients` endpoints. Future client-feature waves add more methods here.
 *
 * All methods are suspend + return raw response types. No `Flow` wrapping, no
 * `DataState` wrapping. Errors propagate as exceptions.
 */
interface ClientApi {

    @POST("clients/{clientId}?command=activate")
    suspend fun activate(
        @Path("clientId") clientId: Int,
        @Body payload: ActivatePayload,
    ): PostClientsClientIdResponse
}
