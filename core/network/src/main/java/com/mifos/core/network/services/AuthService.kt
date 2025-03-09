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

import org.openapitools.client.models.PostAuthenticationRequest
import org.openapitools.client.models.PostAuthenticationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("authentication")
    suspend fun authenticate(
        @Body postAuthenticationRequest: PostAuthenticationRequest,
    ): PostAuthenticationResponse
}
