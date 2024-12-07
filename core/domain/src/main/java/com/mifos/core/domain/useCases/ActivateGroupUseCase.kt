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
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ActivatePayload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class ActivateGroupUseCase @Inject constructor(private val activateRepository: ActivateRepository) {

    suspend operator fun invoke(
        groupId: Int,
        groupPayload: ActivatePayload,
    ): Flow<Resource<GenericResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            activateRepository.activateGroup(groupId, groupPayload)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(response: GenericResponse) {
                        trySend(Resource.Success(response))
                    }
                })
            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}
