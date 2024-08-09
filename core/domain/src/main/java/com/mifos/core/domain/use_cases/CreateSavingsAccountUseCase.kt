package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.SavingsPayload
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.objects.client.Savings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (12:19 PM)
 */

class CreateSavingsAccountUseCase @Inject constructor(private val repository: SavingsAccountRepository) {

    suspend operator fun invoke(savingsPayload: SavingsPayload?): Flow<Resource<Savings?>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.createSavingsAccount(savingsPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Savings?>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(savings: Savings?) {
                        trySend(Resource.Success(savings))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            send(Resource.Error(exception.message.toString()))
        }
    }
}