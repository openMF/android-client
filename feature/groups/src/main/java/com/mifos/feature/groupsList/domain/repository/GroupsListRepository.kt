package com.mifos.feature.groupsList.domain.repository

import androidx.paging.PagingData
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow

interface GroupsListRepository {

    fun getAllGroups(limit: Int): Flow<PagingData<Group>>

    fun getAllLocalGroups(): Flow<List<Group>>
}