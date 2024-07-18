package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientIdentifiersRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.apache.fineract.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class DeleteIdentifierUseCase @Inject constructor(private val repository: ClientIdentifiersRepository) {

    suspend operator fun invoke(
        clientId: Int,
        identifierId: Int
    ): Flow<Resource<DeleteClientsClientIdIdentifiersIdentifierIdResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            repository.deleteClientIdentifier(clientId = clientId, identifierId = identifierId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object :
                    Subscriber<DeleteClientsClientIdIdentifiersIdentifierIdResponse>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(response: DeleteClientsClientIdIdentifiersIdentifierIdResponse?) {
                        trySend(Resource.Success(response))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}