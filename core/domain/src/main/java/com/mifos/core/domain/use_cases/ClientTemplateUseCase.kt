package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.objects.templates.clients.ClientsTemplate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 10/08/2024 (8:27 PM)
 */
class ClientTemplateUseCase @Inject constructor(private val repository: CreateNewClientRepository) {

    suspend operator fun invoke(): Flow<Resource<ClientsTemplate>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.clientTemplate()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<ClientsTemplate>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(clientsTemplate: ClientsTemplate) {
                            trySend(Resource.Success(clientsTemplate))
                        }
                    })

                awaitClose { channel.close() }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
}