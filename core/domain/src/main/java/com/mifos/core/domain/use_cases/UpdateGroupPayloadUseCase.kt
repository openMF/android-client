package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.objects.group.GroupPayload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 01/08/2024 (10:26 PM)
 */
class UpdateGroupPayloadUseCase @Inject constructor(private val repository: SyncGroupPayloadsRepository) {

    suspend operator fun invoke(groupPayload :  GroupPayload): Flow<Resource<GroupPayload>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.updateGroupPayload(groupPayload)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<GroupPayload>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(groupPayload: GroupPayload) {
                            trySend(Resource.Success(groupPayload))
                        }
                    })

                awaitClose { channel.close() }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }

}