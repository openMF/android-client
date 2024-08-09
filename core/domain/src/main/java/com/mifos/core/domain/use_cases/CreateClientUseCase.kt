package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.group.GroupWithAssociations
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 08/08/2024 (9:48 AM)
 */
class CreateClientUseCase @Inject constructor(private val repository: DataTableListRepository) {

    suspend operator fun invoke(clientPayload: ClientPayload): Flow<Resource<Client>> = callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.createClient(clientPayload)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<Client>() {
                        override fun onCompleted() {}

                        override fun onError(exception: Throwable) {
                            trySend(Resource.Error(exception.message.toString()))
                        }

                        override fun onNext(client: Client) {
                            trySend(Resource.Success(client))
                        }
                    })
                awaitClose { channel.close() }
            } catch (exception: Exception) {
                trySend(Resource.Error(exception.message.toString()))
            }
        }
}