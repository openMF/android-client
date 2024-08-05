package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.zipmodels.GroupAndGroupAccounts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 01/08/2024 (8:23 AM)
 */

class GetGroupOfficesUseCase @Inject constructor(private val repository: CreateNewGroupRepository) {

    suspend operator fun invoke(): Flow<Resource<List<Office>>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.offices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Office>>() {
                    override fun onCompleted() { }

                    override fun onError(e: Throwable) {
                        trySend(Resource.Error(e.message.toString()))
                    }

                    override fun onNext(offices: List<Office>) {
                        trySend(Resource.Success(offices))
                    }
                })

            awaitClose { channel.close() }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message.toString()))
        }
    }
}