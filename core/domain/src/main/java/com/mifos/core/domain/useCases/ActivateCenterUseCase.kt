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
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.objects.client.ActivatePayload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.apache.fineract.client.models.PostCentersCenterIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class ActivateCenterUseCase @Inject constructor(private val activateRepository: ActivateRepository) {

    suspend operator fun invoke(
        centerId: Int,
        centerPayload: ActivatePayload,
    ): Flow<Resource<PostCentersCenterIdResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            activateRepository.activateCenter(centerId, centerPayload)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(object : Subscriber<PostCentersCenterIdResponse>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(response: PostCentersCenterIdResponse) {
                        trySend(Resource.Success(response))
                    }
                })
            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}
