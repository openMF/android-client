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
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.entity.accounts.savings.SavingsAccountTransactionRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (4:55 PM)
 */
class UpdateLoanRepaymentTransactionSavingsUseCase @Inject constructor(
    private val repository: SyncSavingsAccountTransactionRepository,
) {

    suspend operator fun invoke(request: SavingsAccountTransactionRequest?): Flow<Resource<SavingsAccountTransactionRequest>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.updateLoanRepaymentTransaction(request!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        object : Subscriber<SavingsAccountTransactionRequest>() {
                            override fun onCompleted() {
                            }

                            override fun onError(e: Throwable) {
                                trySend(Resource.Error(e.message.toString()))
                            }

                            override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest?) {
                                trySend(Resource.Success(savingsAccountTransactionRequest))
                            }
                        },
                    )

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                send(Resource.Error(exception.message.toString()))
            }
        }
}
