/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.auth.api

import com.mifos.core.model.network.PostAuthenticationRequest
import com.mifos.core.model.network.PostAuthenticationResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

/**
 * Fineract `authentication` endpoints. Future auth-feature waves (OTP, refresh, logout)
 * add more methods here.
 */
interface AuthApi {

    @POST("authentication")
    suspend fun authenticate(
        @Body request: PostAuthenticationRequest,
    ): PostAuthenticationResponse
}
