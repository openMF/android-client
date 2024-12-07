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
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.objects.zipmodels.GroupAndGroupAccounts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class GetGroupDetailsUseCase @Inject constructor(private val repository: GroupDetailsRepository) {

    suspend operator fun invoke(groupId: Int): Flow<Resource<GroupAndGroupAccounts>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())
                Observable.combineLatest(
                    repository.getGroup(groupId),
                    repository.getGroupAccounts(groupId),
                ) { group, groupAccounts -> GroupAndGroupAccounts(group, groupAccounts) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<GroupAndGroupAccounts>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(groupAndGroupAccounts: GroupAndGroupAccounts) {
                            trySend(Resource.Success(groupAndGroupAccounts))
                        }
                    })

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                trySend(Resource.Error(exception.message.toString()))
            }
        }
}
