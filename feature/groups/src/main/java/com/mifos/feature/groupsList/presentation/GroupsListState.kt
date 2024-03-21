package com.mifos.feature.groupsList.presentation

import androidx.paging.PagingData
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow

sealed interface GroupsListState {

    data object Loading : GroupsListState

    data class ShowGroupsList(val groups: Flow<PagingData<Group>>) : GroupsListState
}