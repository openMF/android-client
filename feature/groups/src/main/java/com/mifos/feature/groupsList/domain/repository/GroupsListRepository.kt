package com.mifos.feature.groupsList.domain.repository

import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow

interface GroupsListRepository {

    suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<Group>

    fun getAllLocalGroups(): Flow<List<Group>>
}