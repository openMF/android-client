package com.mifos.feature.client.clientDetails.domain.usecase

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientDetailsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class DeleteClientImageUseCase @Inject constructor(private val repository: ClientDetailsRepository) {

    operator fun invoke(clientId: Int): Flow<Resource<ResponseBody>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            repository.deleteClientImage(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        trySend(Resource.Error("Failed to delete image"))
                    }

                    override fun onNext(response: ResponseBody) {
                        trySend(Resource.Success(response))
                    }
                })

            awaitClose { channel.close() }

        } catch (e: Exception) {
            trySend(Resource.Error(e.message.toString()))
        }
    }

}