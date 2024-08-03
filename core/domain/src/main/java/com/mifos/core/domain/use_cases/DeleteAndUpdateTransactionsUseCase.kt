package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (10:08 PM)
 */
class DeleteAndUpdateTransactionsUseCase @Inject constructor(private val repository: SyncSavingsAccountTransactionRepository) {

    suspend operator fun invoke(savingsAccountId: Int): Flow<Resource<List<SavingsAccountTransactionRequest>>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.deleteAndUpdateTransactions(savingsAccountId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                        override fun onCompleted() {
                        }

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(savingsAccountTransactionRequests: List<SavingsAccountTransactionRequest>) {
                            trySend(Resource.Success(savingsAccountTransactionRequests))
                        }
                    })

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                send(Resource.Error(exception.message.toString()))
            }
        }

}