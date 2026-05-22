/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.auth

import com.mifos.core.model.network.PostAuthenticationResponse

interface LoginRepository {

    /**
     * Sends credentials to Fineract's `/authentication` endpoint. Returns the typed
     * response on success; throws on transport/HTTP failure (caller wraps in
     * `SubmitHandler.submit { ... }` for structured Submitting / Submitted / Failed
     * lifecycle).
     */
    suspend fun login(username: String, password: String): PostAuthenticationResponse
}
