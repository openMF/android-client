/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.auth.impl

import com.mifos.core.data.auth.LoginRepository
import com.mifos.core.network.auth.api.AuthApi
import com.mifos.core.model.network.PostAuthenticationRequest
import com.mifos.core.model.network.PostAuthenticationResponse

class LoginRepositoryImpl(
    private val authApi: AuthApi,
) : LoginRepository {

    override suspend fun login(username: String, password: String): PostAuthenticationResponse =
        authApi.authenticate(
            PostAuthenticationRequest(username = username.trim(), password = password.trim()),
        )
}
