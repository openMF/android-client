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
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.objects.zipmodels.ClientAndClientAccounts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class GetClientDetailsUseCase @Inject constructor(private val repository: ClientDetailsRepository) {

    operator fun invoke(clientId: Int): Flow<Resource<ClientAndClientAccounts>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            Observable.zip(
                repository.getClientAccounts(clientId),
                repository.getClient(clientId),
            ) { clientAccounts, client ->
                val clientAndClientAccounts = ClientAndClientAccounts()
                clientAndClientAccounts.client = client
                clientAndClientAccounts.clientAccounts = clientAccounts
                clientAndClientAccounts
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientAndClientAccounts>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(clientAndClientAccounts: ClientAndClientAccounts) {
                        trySend(Resource.Success(clientAndClientAccounts))
                    }
                })
            awaitClose { channel.close() }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message.toString()))
        }
    }
}
