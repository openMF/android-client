package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.LoansPayload
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.objects.accounts.loan.Loans
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class CreateLoanAccountUseCase @Inject constructor(private val loanAccountRepository: LoanAccountRepository) {

    suspend operator fun invoke(loansPayload: LoansPayload): Flow<Resource<Loans>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            loanAccountRepository.createLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Loans>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(loans: Loans) {
                        trySend(Resource.Success(loans))
                    }
                })

            awaitClose { channel.close() }

        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}