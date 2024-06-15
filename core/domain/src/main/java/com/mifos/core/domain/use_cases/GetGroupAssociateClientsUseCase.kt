package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.GroupWithAssociations
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class GetGroupAssociateClientsUseCase @Inject constructor(private val groupDetailsRepository: GroupDetailsRepository) {

    suspend operator fun invoke(groupId: Int): Flow<Resource<List<Client>>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            groupDetailsRepository.getGroupWithAssociations(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupWithAssociations>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(groupWithAssociations: GroupWithAssociations) {
                        trySend(Resource.Success(groupWithAssociations.clientMembers))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}