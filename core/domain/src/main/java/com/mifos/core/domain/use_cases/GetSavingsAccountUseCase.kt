package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Savings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (12:53 PM)
 */
class GetSavingsAccountUseCase @Inject constructor(private val repository: SavingsAccountSummaryRepository) {

    suspend operator fun invoke(
        type: String?,
        accountId: Int,
        association: String?
    ): Flow<Resource<SavingsAccountWithAssociations>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.getSavingsAccount(type, accountId, association)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountWithAssociations>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(savingsAccountWithAssociations: SavingsAccountWithAssociations) {
                        trySend(Resource.Success(savingsAccountWithAssociations))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            send(Resource.Error(exception.message.toString()))
        }
    }
}