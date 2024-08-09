package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.CenterPayload
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.objects.response.SaveResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 08/08/2024 (2:22 PM)
 */
class AllDatabaseCenterPayloadUseCase @Inject constructor(private val repository: SyncCenterPayloadsRepository) {

    suspend operator fun invoke(): Flow<Resource<List<CenterPayload>>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())

                repository.allDatabaseCenterPayload()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<List<CenterPayload>>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(centerPayloads: List<CenterPayload>) {
                            trySend(Resource.Success(centerPayloads))
                        }
                    })

                awaitClose { channel.close() }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
}