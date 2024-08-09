package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (1:37 PM)
 */
class GetSavingsAccountTransactionUseCase @Inject constructor(private val repository: SavingsAccountTransactionRepository) {

    suspend operator fun invoke(accountId: Int): Flow<Resource<SavingsAccountTransactionRequest>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.getSavingsAccountTransaction(accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
                        trySend(Resource.Success(savingsAccountTransactionRequest))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            send(Resource.Error(exception.message.toString()))
        }
    }
}