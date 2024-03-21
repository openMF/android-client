package com.mifos.feature.groupsList.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import com.mifos.feature.groupsList.domain.use_case.GroupsListPagingDataSource
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
    private val prefManager: PrefManager,
) : GroupsListRepository {
    override fun getAllGroups(limit: Int): Flow<PagingData<Group>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                GroupsListPagingDataSource(dataManager, prefManager, limit)
            }
        ).flow
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