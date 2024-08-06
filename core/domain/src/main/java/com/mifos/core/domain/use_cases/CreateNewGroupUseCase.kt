package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 01/08/2024 (9:18 AM)
 */
class CreateNewGroupUseCase @Inject constructor(private val repository: CreateNewGroupRepository) {

    suspend operator fun invoke(groupPayload: GroupPayload): Flow<Resource<SaveResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.createGroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SaveResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(saveResponse: SaveResponse) {
                        trySend(Resource.Success(saveResponse))
                    }
                })

            awaitClose { channel.close() }
        }
        catch (e: Exception){
            trySend(Resource.Error(e.message.toString()))
        }
    }
}