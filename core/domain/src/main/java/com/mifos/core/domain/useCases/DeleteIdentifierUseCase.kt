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
import com.mifos.core.data.repository.ClientIdentifiersRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.apache.fineract.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class DeleteIdentifierUseCase @Inject constructor(private val repository: ClientIdentifiersRepository) {

    suspend operator fun invoke(
        clientId: Int,
        identifierId: Int,
    ): Flow<Resource<DeleteClientsClientIdIdentifiersIdentifierIdResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            repository.deleteClientIdentifier(clientId = clientId, identifierId = identifierId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object :
                    Subscriber<DeleteClientsClientIdIdentifiersIdentifierIdResponse>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(response: DeleteClientsClientIdIdentifiersIdentifierIdResponse?) {
                        trySend(Resource.Success(response))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}
