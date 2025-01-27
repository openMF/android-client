/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.adapter.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

internal class ResponseCallAdapter<T>(
    private val responseType: Type,
) : CallAdapter<T, Flow<Response<T>>> {

    override fun responseType(): Type =
        responseType

    override fun adapt(call: Call<T>): Flow<Response<T>> = flow {
        emit(
            suspendCancellableCoroutine { continuation ->
                call.registerCallback(continuation) { response ->
                    continuation.resumeWith(
                        kotlin.runCatching {
                            if (response.isSuccessful) {
                                response
                            } else {
                                throw HttpException(response)
                            }
                        },
                    )
                }

                call.registerOnCancellation(continuation)
            },
        )
    }
}
