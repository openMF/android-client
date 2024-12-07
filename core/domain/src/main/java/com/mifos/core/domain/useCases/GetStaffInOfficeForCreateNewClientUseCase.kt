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
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.objects.organisation.Staff
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 10/08/2024 (8:43 PM)
 */
class GetStaffInOfficeForCreateNewClientUseCase @Inject constructor(private val repository: CreateNewClientRepository) {

    suspend operator fun invoke(officeId: Int): Flow<Resource<List<Staff>>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.getStaffInOffice(officeId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<List<Staff>>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(staffList: List<Staff>) {
                            trySend(Resource.Success(staffList))
                        }
                    })

                awaitClose { channel.close() }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
}
