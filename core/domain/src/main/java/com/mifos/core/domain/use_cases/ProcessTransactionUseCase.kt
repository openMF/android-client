package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.mifoserror.MifosError
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (1:29 PM)
 */

class ProcessTransactionUseCase @Inject constructor(private val repository: SavingsAccountTransactionRepository) {

    suspend operator fun invoke(
        endpoint: String?,
        accountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequest
    ): Flow<Resource<SavingsAccountTransactionResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.processTransaction(endpoint, accountId, transactionType, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse) {
                        trySend(Resource.Success(savingsAccountTransactionResponse))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            send(Resource.Error(exception.message.toString()))
        }
    }
}