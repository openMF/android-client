package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.objects.templates.loans.LoanTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class GetLoansAccountTemplateUseCase @Inject constructor(private val loanAccountRepository: LoanAccountRepository) {

    suspend operator fun invoke(clientId: Int, productId: Int): Flow<Resource<LoanTemplate>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())
                loanAccountRepository.getLoansAccountTemplate(clientId, productId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<LoanTemplate>() {
                        override fun onCompleted() {}

                        override fun onError(exception: Throwable) {
                            trySend(Resource.Error(exception.message.toString()))
                        }

                        override fun onNext(loanTemplate: LoanTemplate?) {
                            trySend(Resource.Success(loanTemplate))
                        }
                    })

                awaitClose { channel.close() }

            } catch (exception: Exception) {
                trySend(Resource.Error(exception.message.toString()))
            }
        }
}