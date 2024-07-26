package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class GetGroupLoansAccountTemplateUseCase @Inject constructor(private val repository: GroupLoanAccountRepository) {

    suspend operator fun invoke(groupId: Int, productId: Int): Flow<Resource<GroupLoanTemplate>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())
                repository.getGroupLoansAccountTemplate(groupId, productId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<GroupLoanTemplate>() {
                        override fun onCompleted() {}

                        override fun onError(exception: Throwable) {
                            trySend(Resource.Error(exception.message.toString()))
                        }

                        override fun onNext(response: GroupLoanTemplate) {
                            trySend(Resource.Success(response))
                        }
                    })
                awaitClose { channel.close() }
            } catch (exception: Exception) {
                trySend(Resource.Error(exception.message.toString()))
            }
        }
}