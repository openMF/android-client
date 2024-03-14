package com.mifos.feature.groupsList.domain.repository

import androidx.paging.PagingData
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow
import rx.Observable

interface GroupsListRepository {

    fun getAllGroups(limit: Int): Flow<PagingData<Group>>

    fun getAllDatabaseGroups(): Observable<Page<Group>>

}