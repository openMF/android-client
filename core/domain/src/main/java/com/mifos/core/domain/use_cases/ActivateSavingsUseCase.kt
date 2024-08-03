package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.SavingsPayload
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.savings.Transaction_Table.savingsAccountId
import com.mifos.core.objects.client.Savings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (12:33 PM)
 */
class ActivateSavingsUseCase @Inject constructor(private val repository: SavingsAccountActivateRepository) {

    suspend operator fun invoke(savingsAccountId : Int, request: HashMap<String, String>): Flow<Resource<GenericResponse>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.activateSavings(savingsAccountId, request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<GenericResponse>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(genericResponse: GenericResponse) {
                            trySend(Resource.Success(genericResponse))
                        }
                    })

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                send(Resource.Error(exception.message.toString()))
            }
        }
}