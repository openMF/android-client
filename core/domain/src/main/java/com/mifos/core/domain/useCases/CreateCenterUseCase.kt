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
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.objects.responses.SaveResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 08/08/2024 (2:08 PM)
 */
class CreateCenterUseCase @Inject constructor(private val repository: SyncCenterPayloadsRepository) {

    suspend operator fun invoke(centerPayload: CenterPayload): Flow<Resource<SaveResponse?>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.createCenter(centerPayload)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<SaveResponse?>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(center: SaveResponse?) {
                            trySend(Resource.Success(center))
                        }
                    })

                awaitClose { channel.close() }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
}
