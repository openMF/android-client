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

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.feature.groups.createNewGroup.CreateNewGroupScreen
import com.mifos.feature.groups.groupDetails.GroupDetailsScreen
import com.mifos.feature.groups.groupList.GroupsListRoute
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity

/**
 * Created by Pronay Sarker on 13/08/2024
 */
fun NavGraphBuilder.groupNavGraph(
    paddingValues: PaddingValues,
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
    navigation(
        startDestination = GroupScreen.GroupListScreen.route,
        route = "groups_screen_graph",
    ) {
        groupListScreenRoute(
            paddingValues = paddingValues,
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

fun NavGraphBuilder.groupListScreenRoute(
    paddingValues: PaddingValues,
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
) {
    composable(route = GroupScreen.GroupListScreen.route) {
        GroupsListRoute(
            paddingValues = paddingValues,
            onAddGroupClick = onAddGroupClick,
            onGroupClick = onGroupClick,
        )
    }
}

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
    composable(
        route = GroupScreen.GroupDetailsScreen.route,
        arguments = listOf(
            navArgument(name = Constants.GROUP_ID, builder = { type = NavType.IntType }),
        ),
    ) {
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

fun NavGraphBuilder.addNewGroupRoute(
    onBackPressed: () -> Unit,
    onGroupCreated: (group: SaveResponse?, userStatus: Boolean) -> Unit,
) {
    composable(route = GroupScreen.CreateNewGroupScreen.route) {
        CreateNewGroupScreen(
            onGroupCreated = onGroupCreated,
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToCreateNewGroupScreen() {
    navigate(GroupScreen.CreateNewGroupScreen.route)
}

fun NavController.navigateToGroupDetailsScreen(groupId: Int) {
    navigate(GroupScreen.GroupDetailsScreen.argument(groupId))
}
