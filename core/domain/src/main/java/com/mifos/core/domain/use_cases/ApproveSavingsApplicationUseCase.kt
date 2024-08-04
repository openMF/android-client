package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import com.mifos.core.objects.accounts.savings.Transaction_Table.savingsAccountId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.internal.operators.OperatorReplay.observeOn
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 04/08/2024 (12:46 PM)
 */
class ApproveSavingsApplicationUseCase @Inject constructor(private val repository: SavingsAccountApprovalRepository) {

    suspend operator fun invoke(savingsAccountId : Int, savingsApproval: SavingsApproval?): Flow<Resource<GenericResponse>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.approveSavingsApplication(savingsAccountId, savingsApproval)
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