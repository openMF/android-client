/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openapitools.client.models.PostAuthenticationResponse

/**
 * Created by Aditya Gupta on 11/02/24.
 */

class LoginUseCase(private val loginRepository: LoginRepository) {

    operator fun invoke(
        username: String,
        password: String,
    ): Flow<Resource<PostAuthenticationResponse>> = flow {
        try {
            emit(Resource.Loading())
            val result = loginRepository.login(username, password)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}
