/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.network.datamanger.DataManagerAuth
import org.openapitools.client.models.PostAuthenticationResponse
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

class LoginRepositoryImp @Inject constructor(private val dataManagerAuth: DataManagerAuth) :
    LoginRepository {

    override suspend fun login(username: String, password: String): PostAuthenticationResponse {
        return dataManagerAuth.login(username, password)
    }
}
