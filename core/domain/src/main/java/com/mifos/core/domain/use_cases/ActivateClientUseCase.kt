package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.objects.client.ActivatePayload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.apache.fineract.client.models.PostClientsClientIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class ActivateClientUseCase @Inject constructor(private val activateRepository: ActivateRepository) {

    suspend operator fun invoke(
        clientId: Int,
        clientPayload: ActivatePayload
    ): Flow<Resource<PostClientsClientIdResponse>> = callbackFlow {
        try {

            trySend(Resource.Loading())
            activateRepository.activateClient(clientId, clientPayload)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(object : Subscriber<PostClientsClientIdResponse>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(response: PostClientsClientIdResponse) {
                        trySend(Resource.Success(response))
                    }
                })
            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}