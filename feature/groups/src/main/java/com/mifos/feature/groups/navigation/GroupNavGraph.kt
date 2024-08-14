package com.mifos.feature.groups.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.response.SaveResponse
import com.mifos.feature.groups.create_new_group.CreateNewGroupScreen
import com.mifos.feature.groups.group_details.GroupDetailsScreen
import com.mifos.feature.groups.group_list.GroupsListRoute
import com.mifos.feature.groups.sync_group_payloads.SyncGroupPayloadsScreenRoute

/**
 * Created by Pronay Sarker on 13/08/2024
 */

fun NavGraphBuilder.groupNavGraph(
    paddingValues: PaddingValues,
    navController: NavController,
    addGroupLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    loadDocumentList: (Int) -> Unit,
    clientListFragment: (List<Client>) -> Unit,
    loadGroupDataTables: (Int) -> Unit,
    loadNotes: (Int) -> Unit,
    loadLoanAccountSummary: (Int) -> Unit,
    loadSavingsAccountSummary: (Int, DepositType) -> Unit,
    activateGroup: (Int) -> Unit
) {
    navigation(
        startDestination = GroupScreen.GroupListScreen.route,
        route = "group_list_route"
    ) {
        groupListScreenRoute(
            paddingValues = paddingValues,
            onAddGroupClick = navController::navigateToCreateNewGroupScreen,
            onGroupClick = navController::navigateToGroupDetailsScreen,
            onSyncClick = { }
        )

        groupDetailsRoute(
            onBackPressed = navController::popBackStack,
            addGroupLoanAccount = addGroupLoanAccount,
            addSavingsAccount = addSavingsAccount,
            loadDocumentList = loadDocumentList,
            clientListFragment = clientListFragment,
            loadGroupDataTables = loadGroupDataTables,
            loadNotes = loadNotes,
            loadLoanAccountSummary = loadLoanAccountSummary,
            loadSavingsAccountSummary = loadSavingsAccountSummary,
            activateGroup = activateGroup
        )

        addNewGroupRoute(
            onGroupCreated = { }
        )

        syncGroupPayloadsRoute(
            onBackPressed = navController::popBackStack
        )
    }
}

fun NavGraphBuilder.groupListScreenRoute(
    paddingValues: PaddingValues,
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
    onSyncClick: (List<Group>) -> Unit
) {
    composable(route = GroupScreen.GroupListScreen.route) {
        GroupsListRoute(
            paddingValues = paddingValues,
            onAddGroupClick = onAddGroupClick,
            onGroupClick = onGroupClick,
            onSyncClick = onSyncClick
        )
    }
}

fun NavGraphBuilder.groupDetailsRoute(
    onBackPressed: () -> Unit,
    addGroupLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    loadDocumentList: (Int) -> Unit,
    clientListFragment: (List<Client>) -> Unit,
    loadGroupDataTables: (Int) -> Unit,
    loadNotes: (Int) -> Unit,
    loadLoanAccountSummary: (Int) -> Unit,
    loadSavingsAccountSummary: (Int, DepositType) -> Unit,
    activateGroup: (Int) -> Unit
) {
    composable(
        route = GroupScreen.GroupDetailsScreen.route,
        arguments = listOf(
            navArgument(name = Constants.GROUP_ID, builder = { type = NavType.IntType })
        )
    ) {
        GroupDetailsScreen(
            onBackPressed = onBackPressed,
            addLoanAccount = addGroupLoanAccount,
            addSavingsAccount = addSavingsAccount,
            documents = loadDocumentList,
            groupClients = clientListFragment,
            moreGroupInfo = loadGroupDataTables,
            notes = loadNotes,
            loanAccountSelected = loadLoanAccountSummary,
            savingsAccountSelected = loadSavingsAccountSummary,
            activateGroup = activateGroup
        )
    }
}

fun NavGraphBuilder.addNewGroupRoute(
    onGroupCreated: (group: SaveResponse?) -> Unit,
) {
    composable(route = GroupScreen.CreateNewGroupScreen.route) {
        CreateNewGroupScreen(
            onGroupCreated = onGroupCreated
        )
    }
}

fun NavGraphBuilder.syncGroupPayloadsRoute(
    onBackPressed: () -> Unit
) {
    composable(route = GroupScreen.SyncGroupPayloadScreen.route) {
        SyncGroupPayloadsScreenRoute(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateToCreateNewGroupScreen() {
    navigate(GroupScreen.CreateNewGroupScreen.route)
}

fun NavController.navigateToGroupDetailsScreen(groupId: Int) {
    navigate(GroupScreen.GroupDetailsScreen.argument(groupId))
}

fun NavController.navigateToSyncGroupPayloadScreen() {
    navigate(GroupScreen.SyncGroupPayloadScreen.route)
}






