package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.objects.zipmodels.GroupAndGroupAccounts
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class GetGroupDetailsUseCase @Inject constructor(private val repository: GroupDetailsRepository) {

    suspend operator fun invoke(groupId: Int): Flow<Resource<GroupAndGroupAccounts>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())
                Observable.combineLatest(
                    repository.getGroup(groupId),
                    repository.getGroupAccounts(groupId)
                ) { group, groupAccounts -> GroupAndGroupAccounts(group, groupAccounts) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<GroupAndGroupAccounts>() {
                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            trySend(Resource.Error(e.message.toString()))
                        }

                        override fun onNext(groupAndGroupAccounts: GroupAndGroupAccounts) {
                            trySend(Resource.Success(groupAndGroupAccounts))
                        }
                    })

                awaitClose { channel.close() }
            } catch (exception: Exception) {
                trySend(Resource.Error(exception.message.toString()))
            }
        }

}