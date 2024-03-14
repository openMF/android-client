package com.mifos.feature.groupsList.presentation

import androidx.paging.PagingData
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow

sealed interface GroupsListState {

    data object Empty: GroupsListState

    data class Error(val message: String): GroupsListState

    data class GroupsFromAPI(val groups: Flow<PagingData<Group>>): GroupsListState

    data class GroupsFromLocalDB(val groups: List<Group>): GroupsListState
}