package com.mifos.feature.groupsList.domain.use_case

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GroupsListPagingDataSource(
    private val dataManager: DataManagerGroups,
    private val prefManager: PrefManager,
    private val limit: Int,
) : PagingSource<Int, Group>() {

    override fun getRefreshKey(state: PagingState<Int, Group>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(10) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Group> {
        val currentOffset = params.key ?: 1
        return try {
            val localGroups = getAllLocalGroups()

            if (!prefManager.userStatus) {
                val (serverGroups, totalGroups) = getGroups(currentOffset, limit)
                val syncableGroups = getSyncableGroups(serverGroups, localGroups)

                LoadResult.Page(
                    data = syncableGroups,
                    prevKey = if (currentOffset <= 0) null else currentOffset - 10,
                    nextKey = if (currentOffset >= totalGroups) null else currentOffset + 10
                )
            }else {
                LoadResult.Page(
                    data = localGroups,
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getGroups(offset: Int, limit: Int): Pair<List<Group>, Int> {
        return suspendCancellableCoroutine {
            dataManager.getGroups(paged = true, offset = offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Group>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        it.resumeWithException(e)
                    }

                    override fun onNext(t: Page<Group>) {
                        it.resume(Pair(t.pageItems, t.totalFilteredRecords))
                    }
                })
        }
    }

    private suspend fun getAllLocalGroups(): List<Group> {
        return suspendCoroutine { continuation ->
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
    }

    private fun getSyncableGroups(
        groupList: List<Group>,
        localGroupList: List<Group>
    ): List<Group> {
        if (localGroupList.isEmpty()) return groupList
        return groupList.map { group ->
            localGroupList.firstOrNull { it.id == group.id }?.let { group.copy(sync = true) }
                ?: group
        }
    }
}