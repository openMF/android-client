package com.mifos.feature.groups.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.objects.group.Group
import com.mifos.feature.groups.group_list.GroupsListRoute

/**
 * Created by Pronay Sarker on 10/08/2024 (6:07 AM)
 */
const val GROUP_LIST_SCREEN_ROUTE = "group_list_route"

fun NavController.navigateToGroupList() {
    this.navigate(GROUP_LIST_SCREEN_ROUTE)
}

fun NavGraphBuilder.groupListScreen(
    paddingValues: PaddingValues,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    onSyncClick: (List<Group>) -> Unit,
) {
    composable(GROUP_LIST_SCREEN_ROUTE) {
        GroupsListRoute(
            paddingValues = paddingValues,
            onAddGroupClick = onAddGroupClick,
            onGroupClick = onGroupClick,
            onSyncClick = onSyncClick
        )
    }
}
