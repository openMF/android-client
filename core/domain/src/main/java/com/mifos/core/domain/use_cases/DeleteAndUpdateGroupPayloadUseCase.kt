package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.CenterPayload
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.domain.R
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
 * Created by Pronay Sarker on 01/08/2024 (9:13 PM)
 */
class DeleteAndUpdateGroupPayloadUseCase @Inject constructor(private val repository: SyncGroupPayloadsRepository) {

    suspend operator fun invoke(id: Int): Flow<Resource<List<GroupPayload>>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.deleteAndUpdateGroupPayloads(id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<List<GroupPayload>>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(groupPayloads: List<GroupPayload>) {
                            trySend(Resource.Success(groupPayloads))
                        }
                    })

                awaitClose { channel.close() }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }

}
