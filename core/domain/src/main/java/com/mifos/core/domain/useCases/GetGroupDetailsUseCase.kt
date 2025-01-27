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
import kotlinx.coroutines.flow.channelFlow
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class GetGroupDetailsUseCase @Inject constructor(private val repository: GroupDetailsRepository) {
    operator fun invoke(groupId: Int): Flow<Resource<GroupAndGroupAccounts>> =
        channelFlow {
            val disposable = Observable.combineLatest(
                repository.getGroup(groupId),
                repository.getGroupAccounts(groupId),
            ) { group, groupAccounts ->
                GroupAndGroupAccounts(group, groupAccounts)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { groupAndGroupAccounts ->
                        trySend(Resource.Success(groupAndGroupAccounts))
                    },
                    { error ->
                        trySend(Resource.Error(error.message ?: "Unknown error occurred"))
                    },
                )

            // Cleanup subscription when the flow is cancelled
            awaitClose {
                disposable.unsubscribe()
            }
        }
}
