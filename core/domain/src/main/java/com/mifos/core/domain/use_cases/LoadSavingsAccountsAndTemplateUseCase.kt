package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.objects.zipmodels.SavingProductsAndTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (4:41 PM)
 */
class LoadSavingsAccountsAndTemplateUseCase @Inject constructor(private val repository: SavingsAccountRepository) {

    suspend operator fun invoke(): Flow<Resource<SavingProductsAndTemplate?>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                Observable.combineLatest(
                    repository.savingsAccounts(),
                    repository.savingsAccountTemplate()
                ) { productSavings, template ->
                    SavingProductsAndTemplate(productSavings, template)
                }.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<SavingProductsAndTemplate?>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(savingProductsAndTemplate: SavingProductsAndTemplate?) {
                            trySend(Resource.Success(savingProductsAndTemplate))
                        }
                    })

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                send(Resource.Error(exception.message.toString()))
            }
        }
}