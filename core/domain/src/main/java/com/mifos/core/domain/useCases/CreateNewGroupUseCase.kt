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
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.modelobjects.responses.SaveResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 01/08/2024 (9:18 AM)
 */
class CreateNewGroupUseCase @Inject constructor(private val repository: CreateNewGroupRepository) {

    suspend operator fun invoke(groupPayload: GroupPayload): Flow<Resource<SaveResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.createGroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SaveResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(saveResponse: SaveResponse) {
                        trySend(Resource.Success(saveResponse))
                    }
                })

            awaitClose { channel.close() }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message.toString()))
        }
    }
}
