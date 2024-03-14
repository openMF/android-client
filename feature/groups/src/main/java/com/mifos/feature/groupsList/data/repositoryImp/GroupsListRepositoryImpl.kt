package com.mifos.feature.groupsList.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import com.mifos.feature.groupsList.domain.use_case.GroupsListPagingDataSource
import kotlinx.coroutines.flow.Flow
import rx.Observable
import javax.inject.Inject

class GroupsListRepositoryImpl @Inject constructor(
    private val dataManager: DataManagerGroups
) : GroupsListRepository {
    override fun getAllGroups(limit: Int): Flow<PagingData<Group>> {
        return Pager(
            config = PagingConfig(pageSize = limit),
            pagingSourceFactory = {
                GroupsListPagingDataSource(dataManager, limit)
            }
        ).flow
    }

    override fun getAllDatabaseGroups(): Observable<Page<Group>> {
        return dataManager.databaseGroups
    }
}