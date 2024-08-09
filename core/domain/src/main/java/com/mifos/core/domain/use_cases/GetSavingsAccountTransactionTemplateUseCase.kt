package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (1:13 PM)
 */
class GetSavingsAccountTransactionTemplateUseCase @Inject constructor(private val repository: SavingsAccountTransactionRepository) {

    suspend operator fun invoke(
        endpoint : String?,
        accountId : Int,
        transactionType: String?
    ): Flow<Resource<SavingsAccountTransactionTemplate>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.getSavingsAccountTransactionTemplate(endpoint, accountId, transactionType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionTemplate>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate) {
                        trySend(Resource.Success(savingsAccountTransactionTemplate))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            send(Resource.Error(exception.message.toString()))
        }
    }

}