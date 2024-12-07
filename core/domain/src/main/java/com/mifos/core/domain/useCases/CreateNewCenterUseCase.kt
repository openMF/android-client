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
import com.mifos.core.data.CenterPayload
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.objects.response.SaveResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class CreateNewCenterUseCase @Inject constructor(private val repository: CreateNewCenterRepository) {

    suspend operator fun invoke(centerPayload: CenterPayload): Flow<Resource<SaveResponse>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())
                repository.createCenter(centerPayload)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<SaveResponse>() {
                        override fun onCompleted() {}

                        override fun onError(exception: Throwable) {
                            trySend(Resource.Error(exception.message.toString()))
                        }

                        override fun onNext(saveResponse: SaveResponse) {
                            trySend(Resource.Success(saveResponse))
                        }
                    })

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                trySend(Resource.Error(exception.message.toString()))
            }
        }
}
