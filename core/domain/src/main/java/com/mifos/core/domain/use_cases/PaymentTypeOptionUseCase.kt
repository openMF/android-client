package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (10:20 PM)
 */
class PaymentTypeOptionUseCase @Inject constructor(private val repository: SyncSavingsAccountTransactionRepository) {

    suspend operator fun invoke(): Flow<Resource<List<PaymentTypeOption>>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            repository.paymentTypeOption()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<PaymentTypeOption>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(paymentOptions : List<PaymentTypeOption>) {
                        trySend(Resource.Success(paymentOptions))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            send(Resource.Error(exception.message.toString()))
        }
    }

}