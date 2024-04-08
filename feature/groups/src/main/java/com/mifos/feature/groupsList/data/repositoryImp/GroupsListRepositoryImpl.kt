package com.mifos.feature.groupsList.data.repositoryImp

import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GroupsListRepositoryImpl @Inject constructor(
    private val dataManager: DataManagerGroups,
) : GroupsListRepository {
    override suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<Group> {
        return suspendCancellableCoroutine { continuation ->
            dataManager
                .getGroups(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Group>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(error: Throwable) {
                        continuation.resumeWithException(error)
                    }

                    override fun onNext(groups: Page<Group>) {
                        continuation.resume(groups.pageItems)
                    }
                })
        }
    }

    override fun getAllLocalGroups(): Flow<List<Group>> {
        return flow {
            val data = suspendCoroutine { continuation ->
                dataManager.databaseGroups
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<Page<Group>>() {
                        override fun onCompleted() {
                        }

                        override fun onError(error: Throwable) {
                            continuation.resumeWithException(error)
                        }

                        override fun onNext(groups: Page<Group>) {
                            continuation.resume(groups.pageItems)
                        }
                    })
            }

            emit(data)
        }
    }
}