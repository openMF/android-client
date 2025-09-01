/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.feature.groups.createNewGroup.CreateNewGroupScreen
import com.mifos.feature.groups.groupDetails.GroupDetailsScreen
import com.mifos.feature.groups.groupList.GroupsListRoute
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import kotlinx.serialization.Serializable

@Serializable
data object GroupNavGraph

fun NavGraphBuilder.groupNavGraph(
    navController: NavController,
    addGroupLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int, Int, Boolean) -> Unit,
    loadDocumentList: (Int, String) -> Unit,
    loadClientList: () -> Unit,
    loadGroupDataTables: (String, Int) -> Unit,
    loadNotes: (Int, String) -> Unit,
    loadLoanAccountSummary: (Int) -> Unit,
    loadSavingsAccountSummary: (Int, SavingAccountDepositTypeEntity) -> Unit,
    activateGroup: (Int, String) -> Unit,
) {
    navigation<GroupNavGraph>(
        startDestination = GroupListRoute,
    ) {
        groupListScreenRoute(
            onAddGroupClick = navController::navigateToCreateNewGroupScreen,
            onGroupClick = navController::navigateToGroupDetailsScreen,
        )

        groupDetailsRoute(
            onBackPressed = navController::popBackStack,
            addGroupLoanAccount = addGroupLoanAccount,
            addSavingsAccount = addSavingsAccount,
            loadDocumentList = loadDocumentList,
            loadClientList = loadClientList,
            loadGroupDataTables = loadGroupDataTables,
            loadNotes = loadNotes,
            loadLoanAccountSummary = loadLoanAccountSummary,
            loadSavingsAccountSummary = loadSavingsAccountSummary,
            activateGroup = activateGroup,
        )

        addNewGroupRoute(
            onGroupCreated = { groups, userStatus ->
                navController.popBackStack()
                if (userStatus == Constants.USER_ONLINE) {
                    groups?.groupId?.let { navController.navigateToGroupDetailsScreen(it) }
                }
            },
            onBackPressed = navController::popBackStack,
        )
    }
}

@Serializable
data object GroupListRoute

fun NavGraphBuilder.groupListScreenRoute(
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
) {
    composable<GroupListRoute> {
        GroupsListRoute(
            onAddGroupClick = onAddGroupClick,
            onGroupClick = onGroupClick,
        )
    }
}

fun NavController.navigateToGroupListScreen() {
    navigate(GroupListRoute)
}

@Serializable
data class GroupDetailsRoute(
    val groupId: Int,
)

fun NavGraphBuilder.groupDetailsRoute(
    onBackPressed: () -> Unit,
    addGroupLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int, Int, Boolean) -> Unit,
    loadDocumentList: (Int, String) -> Unit,
    loadClientList: () -> Unit,
    loadGroupDataTables: (String, Int) -> Unit,
    loadNotes: (Int, String) -> Unit,
    loadLoanAccountSummary: (Int) -> Unit,
    loadSavingsAccountSummary: (Int, SavingAccountDepositTypeEntity) -> Unit,
    activateGroup: (Int, String) -> Unit,
) {
    composable<GroupDetailsRoute> {
        GroupDetailsScreen(
            onBackPressed = onBackPressed,
            addLoanAccount = addGroupLoanAccount,
            addSavingsAccount = addSavingsAccount,
            documents = loadDocumentList,
            groupClients = loadClientList,
            moreGroupInfo = loadGroupDataTables,
            notes = loadNotes,
            loanAccountSelected = loadLoanAccountSummary,
            savingsAccountSelected = loadSavingsAccountSummary,
            activateGroup = activateGroup,
        )
    }
}

fun NavController.navigateToGroupDetailsScreen(groupId: Int) {
    navigate(GroupDetailsRoute(groupId))
}

@Serializable
data object AddNewGroupRoute

fun NavGraphBuilder.addNewGroupRoute(
    onBackPressed: () -> Unit,
    onGroupCreated: (group: SaveResponse?, userStatus: Boolean) -> Unit,
) {
    composable<AddNewGroupRoute> {
        CreateNewGroupScreen(
            onGroupCreated = onGroupCreated,
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToCreateNewGroupScreen() {
    navigate(AddNewGroupRoute)
}
