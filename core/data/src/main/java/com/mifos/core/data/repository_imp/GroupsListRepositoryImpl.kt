package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    override suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<Group> =
        dataManager.getGroups(paged, offset, limit).pageItems

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