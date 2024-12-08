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
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.fineract.client.models.PostAuthenticationResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
            val result = login(username, password)
            emit(result)
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    suspend fun login(username: String, password: String): Resource<PostAuthenticationResponse> {
        return suspendCancellableCoroutine { continuation ->
            loginRepository.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<PostAuthenticationResponse>() {
                    override fun onNext(user: PostAuthenticationResponse) {
                        continuation.resume(Resource.Success(user))
                    }

                    override fun onCompleted() {
                        // No operation needed
                    }

                    override fun onError(e: Throwable) {
                        continuation.resumeWithException(e)
                    }
                })
        }
    }
}
